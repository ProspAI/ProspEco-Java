package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.MetaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>MetaService</h1>
 * Classe de serviço responsável pela gestão das metas de consumo dos usuários,
 * incluindo criação, atualização, exclusão e consulta de metas.
 */
@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cria uma nova meta para um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @param metaRequestDTO DTO contendo os dados da meta.
     * @return DTO de resposta contendo os dados da meta criada.
     */
    @Transactional
    public MetaResponseDTO criarMeta(Long usuarioId, MetaRequestDTO metaRequestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Meta meta = Meta.builder()
                .consumoAlvo(metaRequestDTO.getConsumoAlvo())
                .dataInicio(metaRequestDTO.getDataInicio())
                .dataFim(metaRequestDTO.getDataFim())
                .atingida(false)
                .usuario(usuario)
                .build();

        Meta metaSalva = metaRepository.save(meta);
        return mapToMetaResponseDTO(metaSalva);
    }

    /**
     * Busca todas as metas de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de DTOs de resposta contendo as metas do usuário.
     */
    @Transactional(readOnly = true)
    public List<MetaResponseDTO> buscarMetasPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return metaRepository.findByUsuario(usuario).stream()
                .map(this::mapToMetaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma meta existente.
     *
     * @param id ID da meta.
     * @param metaRequestDTO DTO contendo os novos dados da meta.
     * @return DTO de resposta contendo os dados da meta atualizada.
     */
    @Transactional
    public MetaResponseDTO atualizarMeta(Long id, MetaRequestDTO metaRequestDTO) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada."));

        meta.setConsumoAlvo(metaRequestDTO.getConsumoAlvo());
        meta.setDataInicio(metaRequestDTO.getDataInicio());
        meta.setDataFim(metaRequestDTO.getDataFim());

        Meta metaAtualizada = metaRepository.save(meta);
        return mapToMetaResponseDTO(metaAtualizada);
    }

    /**
     * Exclui uma meta pelo ID.
     *
     * @param id ID da meta a ser excluída.
     */
    @Transactional
    public void excluirMeta(Long id) {
        if (!metaRepository.existsById(id)) {
            throw new EntityNotFoundException("Meta não encontrada.");
        }
        metaRepository.deleteById(id);
    }

    /**
     * Marca uma meta como atingida.
     *
     * @param id ID da meta.
     */
    @Transactional
    public void marcarMetaComoAtingida(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada."));

        meta.setAtingida(true);
        metaRepository.save(meta);
    }

    /**
     * Mapeia um objeto Meta para MetaResponseDTO.
     *
     * @param meta Meta a ser mapeada.
     * @return DTO de resposta da meta.
     */
    private MetaResponseDTO mapToMetaResponseDTO(Meta meta) {
        return MetaResponseDTO.builder()
                .id(meta.getId())
                .consumoAlvo(meta.getConsumoAlvo())
                .dataInicio(meta.getDataInicio())
                .dataFim(meta.getDataFim())
                .atingida(meta.getAtingida())
                .usuarioId(meta.getUsuario().getId())
                .build();
    }
}
