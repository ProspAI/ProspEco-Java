package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUsuarioProducer {

    private final KafkaTemplate<String, UsuarioResponseDTO> kafkaTemplate;
    private static final String TOPIC = "usuario";

    public void enviarUsuario(UsuarioResponseDTO usuarioResponseDTO) {
        kafkaTemplate.send(TOPIC, usuarioResponseDTO);
    }
}
