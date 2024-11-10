package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaRegistroConsumoProducer {

    private final KafkaTemplate<String, RegistroConsumoResponseDTO> kafkaTemplate;
    private static final String TOPIC = "registro-consumo";

    public void enviarRegistroConsumo(RegistroConsumoResponseDTO registroConsumoResponseDTO) {
        kafkaTemplate.send(TOPIC, registroConsumoResponseDTO);
    }
}
