package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * <h1>NotificacaoResponseDTO</h1>
 * DTO para enviar dados de uma notificação nas respostas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoResponseDTO {

    /**
     * Identificador único da notificação.
     */
    private Long id;

    /**
     * Mensagem da notificação.
     */
    private String mensagem;

    /**
     * Data e hora em que a notificação foi enviada.
     */
    private LocalDateTime dataHora;

    /**
     * Indica se a notificação foi lida.
     */
    private Boolean lida;

    /**
     * Identificador do usuário que recebeu a notificação.
     */
    private Long usuarioId;
}
