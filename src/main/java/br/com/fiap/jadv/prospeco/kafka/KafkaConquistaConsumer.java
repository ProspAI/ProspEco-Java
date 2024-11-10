package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConquistaConsumer {

    @KafkaListener(topics = "conquista", groupId = "conquista-consumer-group")
    public void consumirConquista(ConquistaResponseDTO conquistaResponseDTO) {
        // Processa o evento da conquista recebida
        System.out.println("Evento de conquista recebido: " + conquistaResponseDTO);
    }
}
