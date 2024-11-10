package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAparelhoConsumer {

    @KafkaListener(topics = "aparelho", groupId = "aparelho-consumer-group")
    public void consumirAparelho(AparelhoResponseDTO aparelhoResponseDTO) {
        // Processa o evento de aparelho recebido
        System.out.println("Evento de aparelho recebido: " + aparelhoResponseDTO);
    }
}
