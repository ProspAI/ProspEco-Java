package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, UsuarioResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.usuario-events}")
    private String usuarioEventsTopic;

    /**
     * Cria um novo usuário e envia um evento ao Kafka.
     *
     * @param requestDTO Dados do usuário a ser criado.
     * @return UsuarioResponseDTO com os dados do usuário criado.
     */
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO requestDTO) {
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("O email já está em uso.");
        }

        Usuario usuario = Usuario.builder()
                .nome(requestDTO.getNome())
                .email(requestDTO.getEmail())
                .senha(passwordEncoder.encode(requestDTO.getSenha()))
                .role("ROLE_USER")
                .pontuacaoEconomia(0.0)
                .build();

        usuarioRepository.save(usuario);

        UsuarioResponseDTO response = convertToResponseDTO(usuario);
        kafkaTemplate.send(usuarioEventsTopic, response);

        return response;
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return UsuarioResponseDTO com os dados do usuário encontrado.
     */
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return convertToResponseDTO(usuario);
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário.
     * @return UsuarioResponseDTO com os dados do usuário encontrado.
     */
    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        return convertToResponseDTO(usuario);
    }

    /**
     * Atualiza os dados de um usuário existente e envia um evento ao Kafka.
     *
     * @param id         ID do usuário a ser atualizado.
     * @param requestDTO Dados de atualização do usuário.
     * @return UsuarioResponseDTO com os dados do usuário atualizado.
     */
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!usuario.getEmail().equals(requestDTO.getEmail()) && usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("O email já está em uso.");
        }

        usuario.setNome(requestDTO.getNome());
        usuario.setEmail(requestDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));

        usuarioRepository.save(usuario);

        UsuarioResponseDTO response = convertToResponseDTO(usuario);
        kafkaTemplate.send(usuarioEventsTopic, response);

        return response;
    }

    /**
     * Exclui um usuário pelo ID e envia um evento ao Kafka.
     *
     * @param id ID do usuário a ser excluído.
     */
    @Transactional
    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UsuarioResponseDTO response = convertToResponseDTO(usuario);
        usuarioRepository.delete(usuario);

        kafkaTemplate.send(usuarioEventsTopic, response);
    }

    /**
     * Converte uma entidade Usuario para UsuarioResponseDTO.
     *
     * @param usuario Entidade Usuario a ser convertida.
     * @return UsuarioResponseDTO correspondente.
     */
    private UsuarioResponseDTO convertToResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .pontuacaoEconomia(usuario.getPontuacaoEconomia())
                .build();
    }
}
