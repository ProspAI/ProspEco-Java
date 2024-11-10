package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>AparelhoService</h1>
 * Classe de serviço responsável por gerenciar as operações relacionadas aos aparelhos dos usuários,
 * como criação, atualização, exclusão e consultas.
 */
@Service
@RequiredArgsConstructor
public class AparelhoService {

    private final AparelhoRepository aparelhoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cria um novo aparelho para um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @param aparelhoRequestDTO DTO contendo os dados do aparelho a ser criado.
     * @return DTO de resposta contendo os dados do aparelho criado.
     */
    @Transactional
    public AparelhoResponseDTO criarAparelho(Long usuarioId, AparelhoRequestDTO aparelhoRequestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (aparelhoRepository.existsByNomeAndUsuario(aparelhoRequestDTO.getNome(), usuario)) {
            throw new IllegalArgumentException("Um aparelho com este nome já existe para o usuário.");
        }

        Aparelho aparelho = Aparelho.builder()
                .nome(aparelhoRequestDTO.getNome())
                .potencia(aparelhoRequestDTO.getPotencia())
                .tipo(aparelhoRequestDTO.getTipo())
                .descricao(aparelhoRequestDTO.getDescricao())
                .usuario(usuario)
                .build();

        Aparelho aparelhoSalvo = aparelhoRepository.save(aparelho);
        return mapToAparelhoResponseDTO(aparelhoSalvo);
    }

    /**
     * Busca todos os aparelhos registrados de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de DTOs de resposta contendo os dados dos aparelhos do usuário.
     */
    @Transactional(readOnly = true)
    public List<AparelhoResponseDTO> buscarAparelhosPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return aparelhoRepository.findByUsuario(usuario).stream()
                .map(this::mapToAparelhoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza os dados de um aparelho específico de um usuário.
     *
     * @param id ID do aparelho.
     * @param aparelhoRequestDTO DTO contendo os novos dados do aparelho.
     * @return DTO de resposta contendo os dados atualizados do aparelho.
     */
    @Transactional
    public AparelhoResponseDTO atualizarAparelho(Long id, AparelhoRequestDTO aparelhoRequestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aparelho não encontrado."));

        aparelho.setNome(aparelhoRequestDTO.getNome());
        aparelho.setPotencia(aparelhoRequestDTO.getPotencia());
        aparelho.setTipo(aparelhoRequestDTO.getTipo());
        aparelho.setDescricao(aparelhoRequestDTO.getDescricao());

        Aparelho aparelhoAtualizado = aparelhoRepository.save(aparelho);
        return mapToAparelhoResponseDTO(aparelhoAtualizado);
    }

    /**
     * Exclui um aparelho pelo ID.
     *
     * @param id ID do aparelho a ser excluído.
     */
    @Transactional
    public void excluirAparelho(Long id) {
        if (!aparelhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Aparelho não encontrado.");
        }
        aparelhoRepository.deleteById(id);
    }

    /**
     * Mapeia um objeto Aparelho para AparelhoResponseDTO.
     *
     * @param aparelho Aparelho a ser mapeado.
     * @return DTO de resposta do aparelho.
     */
    private AparelhoResponseDTO mapToAparelhoResponseDTO(Aparelho aparelho) {
        return AparelhoResponseDTO.builder()
                .id(aparelho.getId())
                .nome(aparelho.getNome())
                .potencia(aparelho.getPotencia())
                .tipo(aparelho.getTipo())
                .descricao(aparelho.getDescricao())
                .usuarioId(aparelho.getUsuario().getId())
                .build();
    }
}
