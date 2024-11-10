package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaBandeiraTarifariaConsumer {

    @KafkaListener(topics = "bandeira-tarifaria", groupId = "bandeira-tarifaria-consumer-group")
    public void consumirBandeiraTarifaria(BandeiraTarifariaResponseDTO bandeiraTarifariaResponseDTO) {
        // Processa o evento de bandeira tarifária recebido
        System.out.println("Evento de bandeira tarifária recebido: " + bandeiraTarifariaResponseDTO);
    }
}
