package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMetaConsumer {

    @KafkaListener(topics = "meta", groupId = "meta-consumer-group")
    public void consumirMeta(MetaResponseDTO metaResponseDTO) {
        // Processa a atualização da meta recebida
        System.out.println("Meta atualizada recebida: " + metaResponseDTO);
    }
}
