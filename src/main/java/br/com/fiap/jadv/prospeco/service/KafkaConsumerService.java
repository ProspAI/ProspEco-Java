package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Processa mensagens do tópico de eventos de aparelhos.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.aparelho-events}", groupId = "prospeco-group")
    public void consumeAparelhoEvent(String message, Acknowledgment ack) {
        processMessage(message, AparelhoResponseDTO.class, "Aparelho");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de bandeiras tarifárias.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.bandeira-tarifaria-events}", groupId = "prospeco-group")
    public void consumeBandeiraEvent(String message, Acknowledgment ack) {
        processMessage(message, BandeiraTarifariaResponseDTO.class, "Bandeira Tarifária");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de conquistas.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.conquista-events}", groupId = "prospeco-group")
    public void consumeConquistaEvent(String message, Acknowledgment ack) {
        processMessage(message, ConquistaResponseDTO.class, "Conquista");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de metas.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.meta-events}", groupId = "prospeco-group")
    public void consumeMetaEvent(String message, Acknowledgment ack) {
        processMessage(message, MetaResponseDTO.class, "Meta");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de notificações.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.notificacao-events}", groupId = "prospeco-group")
    public void consumeNotificacaoEvent(String message, Acknowledgment ack) {
        processMessage(message, NotificacaoResponseDTO.class, "Notificação");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de recomendações.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.recomendacao-events}", groupId = "prospeco-group")
    public void consumeRecomendacaoEvent(String message, Acknowledgment ack) {
        processMessage(message, RecomendacaoResponseDTO.class, "Recomendação");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de registros de consumo.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.registro-consumo-events}", groupId = "prospeco-group")
    public void consumeRegistroConsumoEvent(String message, Acknowledgment ack) {
        processMessage(message, RegistroConsumoResponseDTO.class, "Registro de Consumo");
        ack.acknowledge();
    }

    /**
     * Processa mensagens do tópico de eventos de usuários.
     *
     * @param message Mensagem recebida do Kafka.
     * @param ack     Acknowledgment para confirmar o processamento.
     */
    @KafkaListener(topics = "${spring.kafka.topic.usuario-events}", groupId = "prospeco-group")
    public void consumeUsuarioEvent(String message, Acknowledgment ack) {
        processMessage(message, UsuarioResponseDTO.class, "Usuário");
        ack.acknowledge();
    }

    /**
     * Método genérico para processar mensagens de eventos do Kafka.
     *
     * @param message   Mensagem em formato JSON.
     * @param dtoClass  Classe do DTO para desserialização.
     * @param eventName Nome do evento para logging.
     * @param <T>       Tipo genérico do DTO.
     */
    private <T> void processMessage(String message, Class<T> dtoClass, String eventName) {
        try {
            T dto = objectMapper.readValue(message, dtoClass);
            logger.info("Evento processado ({}): {}", eventName, dto);
        } catch (Exception e) {
            logger.error("Erro ao processar evento ({}): {}", eventName, e.getMessage(), e);
        }
    }
}
