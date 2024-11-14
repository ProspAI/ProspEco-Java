// UsuarioService.java
package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaUsuarioProducer;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaUsuarioProducer kafkaUsuarioProducer;

    /**
     * Cria um novo usuário no sistema.
     *
     * @param usuarioRequestDTO DTO contendo os dados do usuário a ser criado.
     * @return DTO de resposta contendo os dados do usuário criado.
     */
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

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return DTO contendo os dados do usuário encontrado.
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        return mapToUsuarioResponseDTO(usuario);
    }

    /**
     * Atualiza as informações de um usuário existente.
     *
     * @param id                 ID do usuário.
     * @param usuarioRequestDTO  DTO contendo os novos dados do usuário.
     * @return DTO de resposta contendo os dados atualizados do usuário.
     */
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

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

    /**
     * Exclui um usuário do sistema.
     *
     * @param id ID do usuário a ser excluído.
     */
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

    /**
     * Método para registrar um usuário do Firebase no banco de dados.
     *
     * @param uid   UID do usuário no Firebase.
     * @param nome  Nome do usuário.
     * @param email Email do usuário.
     * @return Usuário registrado no banco de dados.
     */
    @Transactional
    public Usuario registrarUsuarioFirebase(String uid, String nome, String email) {
        // Verifica novamente se o usuário já existe para evitar duplicatas
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(uid)) // Opcional: considerar uma estratégia diferente
                .role("ROLE_USER")
                .pontuacaoEconomia(0.0)
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Enviar evento de novo usuário ao Kafka
        UsuarioResponseDTO usuarioResponseDTO = mapToUsuarioResponseDTO(usuarioSalvo);
        kafkaUsuarioProducer.enviarUsuario(usuarioResponseDTO);

        return usuarioSalvo;
    }

    /**
     * Método para buscar um usuário por email.
     *
     * @param email Email do usuário.
     * @return Usuário se encontrado, ou null caso contrário.
     */
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorEmail(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        return usuarioOptional.orElse(null);
    }

    /**
     * Mapeia a entidade Usuario para o DTO de resposta.
     *
     * @param usuario Entidade Usuario.
     * @return DTO de resposta.
     */
    private UsuarioResponseDTO mapToUsuarioResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .pontuacaoEconomia(usuario.getPontuacaoEconomia())
                .build();
    }

    /**
     * Carrega os detalhes do usuário pelo email.
     *
     * @param username Email do usuário.
     * @return Detalhes do usuário.
     * @throws UsernameNotFoundException Se o usuário não for encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + username));
    }
}
