package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * <h1>RecomendacaoResponseDTO</h1>
 * DTO para enviar dados de uma recomendação nas respostas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecomendacaoResponseDTO {

    /**
     * Identificador único da recomendação.
     */
    private Long id;

    /**
     * Mensagem da recomendação.
     */
    private String mensagem;

    /**
     * Data e hora em que a recomendação foi gerada.
     */
    private LocalDateTime dataHora;

    /**
     * Identificador do usuário que recebeu a recomendação.
     */
    private Long usuarioId;
}
