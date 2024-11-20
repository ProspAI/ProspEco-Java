package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          KafkaProducerService kafkaProducerService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todos os usuários.
     *
     * @return Lista de UsuarioResponseDTO.
     */
    public List<UsuarioResponseDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return Optional de UsuarioResponseDTO.
     */
    public Optional<UsuarioResponseDTO> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria um novo usuário.
     *
     * @param requestDTO Dados do usuário.
     * @return UsuarioResponseDTO com os dados do usuário criado.
     */
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO requestDTO) {
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("O email já está em uso.");
        }

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(requestDTO, usuario);
        usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        usuario.setRole("ROLE_USER"); // Define o papel padrão
        usuario.setPontuacaoEconomia(0.0);

        Usuario novoUsuario = usuarioRepository.save(usuario);

        // Enviar evento ao Kafka
        try {
            UsuarioResponseDTO responseDTO = toResponseDTO(novoUsuario);
            kafkaProducerService.sendMessage("usuario-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novoUsuario);
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id         ID do usuário.
     * @param requestDTO Dados atualizados do usuário.
     * @return UsuarioResponseDTO com os dados atualizados.
     */
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!usuario.getEmail().equals(requestDTO.getEmail()) &&
                usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("O email já está em uso.");
        }

        usuario.setNome(requestDTO.getNome());
        usuario.setEmail(requestDTO.getEmail());

        if (requestDTO.getSenha() != null && !requestDTO.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(requestDTO.getSenha()));
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        // Enviar evento ao Kafka
        try {
            UsuarioResponseDTO responseDTO = toResponseDTO(usuarioAtualizado);
            kafkaProducerService.sendMessage("usuario-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(usuarioAtualizado);
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário.
     * @return UsuarioResponseDTO com os dados do usuário.
     */
    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));
        return toResponseDTO(usuario);
    }

    /**
     * Exclui um usuário.
     *
     * @param id ID do usuário.
     */
    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);

        // Enviar evento ao Kafka
        try {
            UsuarioResponseDTO responseDTO = toResponseDTO(usuario);
            kafkaProducerService.sendMessage("usuario-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte uma entidade Usuario para UsuarioResponseDTO.
     *
     * @param usuario Entidade a ser convertida.
     * @return UsuarioResponseDTO correspondente.
     */
    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        BeanUtils.copyProperties(usuario, responseDTO);
        return responseDTO;
    }
}
