package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.dto.response.*;
import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "br.com.fiap.jadv.prospeco.dto.response, br.com.fiap.jadv.prospeco.dto.request");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, clazz.getName());
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(clazz));
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createKafkaListenerContainerFactory(Class<T> clazz) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(clazz));
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 3))); // Retentativa com 1 segundo de intervalo e 3 tentativas
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AparelhoResponseDTO> aparelhoKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for AparelhoResponseDTO");
        return createKafkaListenerContainerFactory(AparelhoResponseDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BandeiraTarifariaResponseDTO> bandeiraKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for BandeiraTarifariaResponseDTO");
        return createKafkaListenerContainerFactory(BandeiraTarifariaResponseDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConquistaResponseDTO> conquistaKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for ConquistaResponseDTO");
        return createKafkaListenerContainerFactory(ConquistaResponseDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MetaResponseDTO> metaKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for MetaResponseDTO");
        return createKafkaListenerContainerFactory(MetaResponseDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UsuarioResponseDTO> usuarioKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for UsuarioResponseDTO");
        return createKafkaListenerContainerFactory(UsuarioResponseDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificacaoRequestDTO> notificacaoKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for NotificacaoRequestDTO");
        return createKafkaListenerContainerFactory(NotificacaoRequestDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RegistroConsumoResponseDTO> registroConsumoKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for RegistroConsumoResponseDTO");
        return createKafkaListenerContainerFactory(RegistroConsumoResponseDTO.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecomendacaoResponseDTO> recomendacaoKafkaListenerContainerFactory() {
        logger.info("Configuring Kafka Listener Container for RecomendacaoResponseDTO");
        return createKafkaListenerContainerFactory(RecomendacaoResponseDTO.class);
    }
}
