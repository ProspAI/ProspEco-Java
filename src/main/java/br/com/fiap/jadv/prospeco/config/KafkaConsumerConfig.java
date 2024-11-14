package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.dto.response.*;
import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "br.com.fiap.jadv.prospeco.dto.response, br.com.fiap.jadv.prospeco.dto.request");
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(clazz));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AparelhoResponseDTO> aparelhoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AparelhoResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AparelhoResponseDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BandeiraTarifariaResponseDTO> bandeiraKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BandeiraTarifariaResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(BandeiraTarifariaResponseDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConquistaResponseDTO> conquistaKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConquistaResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(ConquistaResponseDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MetaResponseDTO> metaKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MetaResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(MetaResponseDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UsuarioResponseDTO> usuarioKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UsuarioResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(UsuarioResponseDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificacaoRequestDTO> notificacaoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificacaoRequestDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(NotificacaoRequestDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RegistroConsumoResponseDTO> registroConsumoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RegistroConsumoResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(RegistroConsumoResponseDTO.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecomendacaoResponseDTO> recomendacaoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RecomendacaoResponseDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(RecomendacaoResponseDTO.class));
        return factory;
    }

    // Adicione outros tipos conforme necessário
}
