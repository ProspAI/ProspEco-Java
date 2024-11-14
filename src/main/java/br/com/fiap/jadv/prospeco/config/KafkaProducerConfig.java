package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
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

    private <T> ProducerFactory<String, T> producerFactory(Class<T> clazz) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AparelhoResponseDTO> aparelhoKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(AparelhoResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, BandeiraTarifariaResponseDTO> bandeiraKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(BandeiraTarifariaResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, ConquistaResponseDTO> conquistaKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(ConquistaResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, MetaResponseDTO> metaKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(MetaResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, UsuarioResponseDTO> usuarioKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(UsuarioResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, NotificacaoRequestDTO> notificacaoKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(NotificacaoRequestDTO.class));
    }

    @Bean
    public KafkaTemplate<String, RegistroConsumoResponseDTO> registroConsumoKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(RegistroConsumoResponseDTO.class));
    }

    @Bean
    public KafkaTemplate<String, RecomendacaoResponseDTO> recomendacaoKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(RecomendacaoResponseDTO.class));
    }
}
