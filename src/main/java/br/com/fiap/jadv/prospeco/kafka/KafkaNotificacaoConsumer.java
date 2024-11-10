package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificacaoConsumer {

    private final NotificacaoService notificacaoService;

    @KafkaListener(topics = "notificacao", groupId = "your-group-id")
    public void consumirNotificacao(NotificacaoRequestDTO notificacaoRequestDTO) {
        notificacaoService.criarNotificacao(notificacaoRequestDTO);
    }
}
