package br.com.fiap.jadv.prospeco.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Envia uma mensagem para um tópico específico no Kafka.
     *
     * @param topic   Nome do tópico.
     * @param message Objeto a ser enviado.
     */
    public void sendMessage(String topic, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, jsonMessage);
            logger.info("Mensagem enviada ao tópico {}: {}", topic, jsonMessage);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para o tópico {}: {}", topic, e.getMessage(), e);
        }
    }
}
