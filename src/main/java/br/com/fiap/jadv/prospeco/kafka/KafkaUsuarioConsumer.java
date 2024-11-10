package br.com.fiap.jadv.prospeco.kafka;

import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUsuarioConsumer {

    @KafkaListener(topics = "usuario", groupId = "usuario-consumer-group")
    public void consumirUsuario(UsuarioResponseDTO usuarioResponseDTO) {
        // Processa o evento do usuário recebido
        System.out.println("Evento de usuário recebido: " + usuarioResponseDTO);
    }
}
