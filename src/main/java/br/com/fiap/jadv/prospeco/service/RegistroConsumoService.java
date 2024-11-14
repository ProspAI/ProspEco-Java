package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.RegistroConsumoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class RegistroConsumoService {

    private final RegistroConsumoRepository registroConsumoRepository;
    private final AparelhoRepository aparelhoRepository;
    private final KafkaTemplate<String, RegistroConsumoResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.registro-consumo-events}")
    private String registroConsumoEventsTopic;

    /**
     * Cria um novo registro de consumo para um aparelho específico e envia um evento ao Kafka.
     *
     * @param requestDTO Dados do novo registro de consumo.
     * @return RegistroConsumoResponseDTO com os dados do registro criado.
     */
    @Transactional
    public RegistroConsumoResponseDTO criarRegistroConsumo(RegistroConsumoRequestDTO requestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(requestDTO.getAparelhoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        RegistroConsumo registro = RegistroConsumo.builder()
                .dataHora(requestDTO.getDataHora())
                .consumo(requestDTO.getConsumo())
                .aparelho(aparelho)
                .build();

        registroConsumoRepository.save(registro);

        RegistroConsumoResponseDTO response = convertToResponseDTO(registro);
        kafkaTemplate.send(registroConsumoEventsTopic, response);

        return response;
    }

    /**
     * Lista os registros de consumo de um aparelho específico com suporte a paginação.
     *
     * @param aparelhoId ID do aparelho.
     * @param pageable   Configuração de paginação.
     * @return Página de RegistroConsumoResponseDTO.
     */
    public Page<RegistroConsumoResponseDTO> listarRegistrosPorAparelho(Long aparelhoId, Pageable pageable) {
        Aparelho aparelho = aparelhoRepository.findById(aparelhoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        return registroConsumoRepository.findByAparelho(aparelho, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Converte uma entidade RegistroConsumo para RegistroConsumoResponseDTO.
     *
     * @param registro Entidade RegistroConsumo a ser convertida.
     * @return RegistroConsumoResponseDTO correspondente.
     */
    private RegistroConsumoResponseDTO convertToResponseDTO(RegistroConsumo registro) {
        return RegistroConsumoResponseDTO.builder()
                .id(registro.getId())
                .dataHora(registro.getDataHora())
                .consumo(registro.getConsumo())
                .aparelhoId(registro.getAparelho().getId())
                .build();
    }
}
