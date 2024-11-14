package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);

    private <T> ProducerFactory<String, T> producerFactory(Class<T> clazz) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // Desativa o cabeçalho __TypeId__
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AparelhoResponseDTO> aparelhoKafkaTemplate() {
        logger.info("Configuring Kafka Template for AparelhoResponseDTO");
        return new KafkaTemplate<>(producerFactory(AparelhoResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, BandeiraTarifariaResponseDTO> bandeiraKafkaTemplate() {
        logger.info("Configuring Kafka Template for BandeiraTarifariaResponseDTO");
        return new KafkaTemplate<>(producerFactory(BandeiraTarifariaResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, ConquistaResponseDTO> conquistaKafkaTemplate() {
        logger.info("Configuring Kafka Template for ConquistaResponseDTO");
        return new KafkaTemplate<>(producerFactory(ConquistaResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, MetaResponseDTO> metaKafkaTemplate() {
        logger.info("Configuring Kafka Template for MetaResponseDTO");
        return new KafkaTemplate<>(producerFactory(MetaResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, UsuarioResponseDTO> usuarioKafkaTemplate() {
        logger.info("Configuring Kafka Template for UsuarioResponseDTO");
        return new KafkaTemplate<>(producerFactory(UsuarioResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, NotificacaoRequestDTO> notificacaoKafkaTemplate() {
        logger.info("Configuring Kafka Template for NotificacaoRequestDTO");
        return new KafkaTemplate<>(producerFactory(NotificacaoRequestDTO.class));
    }

    @Bean
    public KafkaTemplate<String, RegistroConsumoResponseDTO> registroConsumoKafkaTemplate() {
        logger.info("Configuring Kafka Template for RegistroConsumoResponseDTO");
        return new KafkaTemplate<>(producerFactory(RegistroConsumoResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, RecomendacaoResponseDTO> recomendacaoKafkaTemplate() {
        logger.info("Configuring Kafka Template for RecomendacaoResponseDTO");
        return new KafkaTemplate<>(producerFactory(RecomendacaoResponseDTO.class));
    }
}
