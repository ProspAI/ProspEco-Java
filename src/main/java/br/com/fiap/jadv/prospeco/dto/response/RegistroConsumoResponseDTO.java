package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * <h1>RegistroConsumoResponseDTO</h1>
 * DTO para enviar dados de um registro de consumo nas respostas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroConsumoResponseDTO {

    /**
     * Identificador único do registro de consumo.
     */
    private Long id;

    /**
     * Data e hora em que o registro foi feito.
     */
    private LocalDateTime dataHora;

    /**
     * Valor do consumo em kWh.
     */
    private Double consumo;

    /**
     * Identificador do aparelho ao qual o registro pertence.
     */
    private Long aparelhoId;
}
