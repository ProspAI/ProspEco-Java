package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaRecomendacaoConsumer {

    @KafkaListener(topics = "recomendacao", groupId = "recomendacao-consumer-group")
    public void consumirRecomendacao(RecomendacaoResponseDTO recomendacaoResponseDTO) {
        // Aqui, processamos a recomendação recebida
        System.out.println("Recomendação recebida: " + recomendacaoResponseDTO);
    }
}
