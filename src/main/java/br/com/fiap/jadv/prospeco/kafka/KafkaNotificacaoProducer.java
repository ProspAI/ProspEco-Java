package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificacaoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "notificacao";

    public void enviarNotificacao(NotificacaoRequestDTO notificacaoRequestDTO) {
        kafkaTemplate.send(TOPIC, notificacaoRequestDTO);
    }
}
