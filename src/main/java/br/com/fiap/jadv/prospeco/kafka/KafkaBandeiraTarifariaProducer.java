package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaBandeiraTarifariaProducer {

    private final KafkaTemplate<String, BandeiraTarifariaResponseDTO> kafkaTemplate;
    private static final String TOPIC = "bandeira-tarifaria";

    public void enviarBandeiraTarifaria(BandeiraTarifariaResponseDTO bandeiraTarifariaResponseDTO) {
        kafkaTemplate.send(TOPIC, bandeiraTarifariaResponseDTO);
    }
}
