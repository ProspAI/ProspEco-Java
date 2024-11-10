package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.RegistroConsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>RegistroConsumoService</h1>
 * Classe de serviço responsável pelo gerenciamento dos registros de consumo dos aparelhos,
 * como criação, consulta e exclusão, com suporte a paginação.
 */
@Service
@RequiredArgsConstructor
public class RegistroConsumoService {

    private final RegistroConsumoRepository registroConsumoRepository;
    private final AparelhoRepository aparelhoRepository;

    /**
     * Cria um novo registro de consumo para um aparelho específico.
     *
     * @param registroConsumoRequestDTO DTO contendo os dados do registro de consumo.
     * @return DTO de resposta contendo os dados do registro de consumo criado.
     */
    @Transactional
    public RegistroConsumoResponseDTO criarRegistroConsumo(RegistroConsumoRequestDTO registroConsumoRequestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(registroConsumoRequestDTO.getAparelhoId())
                .orElseThrow(() -> new EntityNotFoundException("Aparelho não encontrado."));

        RegistroConsumo registroConsumo = RegistroConsumo.builder()
                .dataHora(registroConsumoRequestDTO.getDataHora())
                .consumo(registroConsumoRequestDTO.getConsumo())
                .aparelho(aparelho)
                .build();

        RegistroConsumo registroSalvo = registroConsumoRepository.save(registroConsumo);
        return mapToRegistroConsumoResponseDTO(registroSalvo);
    }

    /**
     * Busca os registros de consumo de um aparelho específico, com suporte a paginação.
     *
     * @param aparelhoId ID do aparelho.
     * @param pageable   Objeto de paginação.
     * @return Página de registros de consumo do aparelho.
     */
    @Transactional(readOnly = true)
    public Page<RegistroConsumoResponseDTO> buscarRegistrosPorAparelho(Long aparelhoId, Pageable pageable) {
        Aparelho aparelho = aparelhoRepository.findById(aparelhoId)
                .orElseThrow(() -> new EntityNotFoundException("Aparelho não encontrado."));

        return registroConsumoRepository.findByAparelho(aparelho, pageable)
                .map(this::mapToRegistroConsumoResponseDTO);
    }

    /**
     * Exclui um registro de consumo pelo ID.
     *
     * @param id ID do registro de consumo a ser excluído.
     */
    @Transactional
    public void excluirRegistroConsumo(Long id) {
        if (!registroConsumoRepository.existsById(id)) {
            throw new EntityNotFoundException("Registro de consumo não encontrado.");
        }
        registroConsumoRepository.deleteById(id);
    }

    /**
     * Mapeia um objeto RegistroConsumo para RegistroConsumoResponseDTO.
     *
     * @param registroConsumo Registro de consumo a ser mapeado.
     * @return DTO de resposta do registro de consumo.
     */
    private RegistroConsumoResponseDTO mapToRegistroConsumoResponseDTO(RegistroConsumo registroConsumo) {
        return RegistroConsumoResponseDTO.builder()
                .id(registroConsumo.getId())
                .dataHora(registroConsumo.getDataHora())
                .consumo(registroConsumo.getConsumo())
                .aparelhoId(registroConsumo.getAparelho().getId())
                .build();
    }
}