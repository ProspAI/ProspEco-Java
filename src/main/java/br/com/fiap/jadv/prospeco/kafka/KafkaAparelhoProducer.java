package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAparelhoProducer {

    private final KafkaTemplate<String, AparelhoResponseDTO> kafkaTemplate;
    private static final String TOPIC = "aparelho";

    public void enviarAparelho(AparelhoResponseDTO aparelhoResponseDTO) {
        kafkaTemplate.send(TOPIC, aparelhoResponseDTO);
    }
}
