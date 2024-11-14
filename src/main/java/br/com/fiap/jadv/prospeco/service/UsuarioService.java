package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaUsuarioProducer;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaUsuarioProducer kafkaUsuarioProducer;

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .nome(usuarioRequestDTO.getNome())
                .email(usuarioRequestDTO.getEmail())
                .senha(passwordEncoder.encode(usuarioRequestDTO.getSenha()))
                .role("ROLE_USER")
                .pontuacaoEconomia(0.0)
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Enviar evento de novo usuário ao Kafka
        UsuarioResponseDTO usuarioResponseDTO = mapToUsuarioResponseDTO(usuarioSalvo);
        kafkaUsuarioProducer.enviarUsuario(usuarioResponseDTO);

        return usuarioResponseDTO;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        return mapToUsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // Verifica se o email está sendo alterado e se já existe
        if (!usuario.getEmail().equals(usuarioRequestDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        if (usuarioRequestDTO.getSenha() != null && !usuarioRequestDTO.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        // Enviar evento de atualização de usuário ao Kafka
        UsuarioResponseDTO usuarioResponseDTO = mapToUsuarioResponseDTO(usuarioAtualizado);
        kafkaUsuarioProducer.enviarUsuario(usuarioResponseDTO);

        return usuarioResponseDTO;
    }

    @Transactional
    public void excluirUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }
        usuarioRepository.deleteById(id);

        // Enviar evento de exclusão de usuário ao Kafka
        UsuarioResponseDTO usuarioResponseDTO = UsuarioResponseDTO.builder().id(id).build();
        kafkaUsuarioProducer.enviarUsuario(usuarioResponseDTO);
    }

    private UsuarioResponseDTO mapToUsuarioResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .pontuacaoEconomia(usuario.getPontuacaoEconomia())
                .build();
    }
}
