package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaRegistroConsumoProducer;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.RegistroConsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistroConsumoService {

    private final RegistroConsumoRepository registroConsumoRepository;
    private final AparelhoRepository aparelhoRepository;
    private final KafkaRegistroConsumoProducer kafkaRegistroConsumoProducer;
    private static final Logger logger = LoggerFactory.getLogger(RegistroConsumoService.class);

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

        // Envia o registro de consumo para o Kafka
        RegistroConsumoResponseDTO responseDTO = mapToRegistroConsumoResponseDTO(registroSalvo);
        kafkaRegistroConsumoProducer.enviarRegistroConsumo(responseDTO);

        logger.info("Registro de consumo criado para o aparelho {}: {}", registroConsumo.getAparelho().getId(), responseDTO);

        return responseDTO;
    }

    @Transactional(readOnly = true)
    public Page<RegistroConsumoResponseDTO> buscarRegistrosPorAparelho(Long aparelhoId, Pageable pageable) {
        Aparelho aparelho = aparelhoRepository.findById(aparelhoId)
                .orElseThrow(() -> new EntityNotFoundException("Aparelho não encontrado."));

        Page<RegistroConsumoResponseDTO> registros = registroConsumoRepository.findByAparelho(aparelho, pageable)
                .map(this::mapToRegistroConsumoResponseDTO);

        logger.info("Registros de consumo recuperados para o aparelho {}: página {} de {}", aparelhoId, pageable.getPageNumber(), registros.getTotalPages());

        return registros;
    }

    @Transactional
    public void excluirRegistroConsumo(Long id) {
        if (!registroConsumoRepository.existsById(id)) {
            throw new EntityNotFoundException("Registro de consumo não encontrado.");
        }
        registroConsumoRepository.deleteById(id);
        logger.info("Registro de consumo excluído: {}", id);
    }

    private RegistroConsumoResponseDTO mapToRegistroConsumoResponseDTO(RegistroConsumo registroConsumo) {
        return RegistroConsumoResponseDTO.builder()
                .id(registroConsumo.getId())
                .dataHora(registroConsumo.getDataHora())
                .consumo(registroConsumo.getConsumo())
                .aparelhoId(registroConsumo.getAparelho().getId())
                .build();
    }
}
