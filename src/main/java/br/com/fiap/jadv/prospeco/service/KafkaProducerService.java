package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, AparelhoResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.aparelho-events}")
    private String aparelhoEventsTopic;

    public void sendAparelhoEvent(AparelhoResponseDTO aparelhoResponse) {
        kafkaTemplate.send(aparelhoEventsTopic, aparelhoResponse);
        logger.info("Mensagem enviada para o Kafka: {}", aparelhoResponse);
    }
}
