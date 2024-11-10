package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaRecomendacaoProducer {

    private final KafkaTemplate<String, RecomendacaoResponseDTO> kafkaTemplate;
    private static final String TOPIC = "recomendacao";

    public void enviarRecomendacao(RecomendacaoResponseDTO recomendacaoResponseDTO) {
        kafkaTemplate.send(TOPIC, recomendacaoResponseDTO);
    }
}
