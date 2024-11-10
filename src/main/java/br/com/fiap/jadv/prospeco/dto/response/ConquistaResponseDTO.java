package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * <h1>ConquistaResponseDTO</h1>
 * DTO para enviar dados de uma conquista nas respostas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConquistaResponseDTO {

    /**
     * Identificador único da conquista.
     */
    private Long id;

    /**
     * Título da conquista.
     */
    private String titulo;

    /**
     * Descrição da conquista.
     */
    private String descricao;

    /**
     * Data e hora em que a conquista foi obtida.
     */
    private LocalDateTime dataConquista;

    /**
     * Identificador do usuário que obteve a conquista.
     */
    private Long usuarioId;
}
