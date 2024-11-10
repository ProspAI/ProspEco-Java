package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaRegistroConsumoConsumer {

    @KafkaListener(topics = "registro-consumo", groupId = "registro-consumo-consumer-group")
    public void consumirRegistroConsumo(RegistroConsumoResponseDTO registroConsumoResponseDTO) {
        // Processa o registro de consumo recebido
        System.out.println("Registro de consumo recebido: " + registroConsumoResponseDTO);
    }
}
