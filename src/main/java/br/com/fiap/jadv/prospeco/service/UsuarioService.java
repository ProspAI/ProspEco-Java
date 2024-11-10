package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>UsuarioService</h1>
 * Classe de serviço responsável por gerenciar as operações relacionadas aos usuários,
 * incluindo criação, atualização, e consultas.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return mapToUsuarioResponseDTO(usuarioSalvo);
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
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        return mapToUsuarioResponseDTO(usuario);
    }

    /**
     * Atualiza as informações de um usuário existente.
     *
     * @param id ID do usuário.
     * @param usuarioRequestDTO DTO contendo os novos dados do usuário.
     * @return DTO de resposta contendo os dados atualizados do usuário.
     */
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        if (usuarioRequestDTO.getSenha() != null && !usuarioRequestDTO.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return mapToUsuarioResponseDTO(usuarioAtualizado);
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
    }

    /**
     * Mapeia um objeto Usuario para UsuarioResponseDTO.
     *
     * @param usuario Usuário a ser mapeado.
     * @return DTO de resposta do usuário.
     */
    private UsuarioResponseDTO mapToUsuarioResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .pontuacaoEconomia(usuario.getPontuacaoEconomia())
                .build();
    }
}
