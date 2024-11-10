package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConquistaProducer {

    private final KafkaTemplate<String, ConquistaResponseDTO> kafkaTemplate;
    private static final String TOPIC = "conquista";

    public void enviarConquista(ConquistaResponseDTO conquistaResponseDTO) {
        kafkaTemplate.send(TOPIC, conquistaResponseDTO);
    }
}
