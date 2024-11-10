package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMetaProducer {

    private final KafkaTemplate<String, MetaResponseDTO> kafkaTemplate;
    private static final String TOPIC = "meta";

    public void enviarMeta(MetaResponseDTO metaResponseDTO) {
        kafkaTemplate.send(TOPIC, metaResponseDTO);
    }
}
