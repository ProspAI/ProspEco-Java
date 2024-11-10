package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;
import java.time.LocalDate;

/**
 * <h1>MetaResponseDTO</h1>
 * DTO para enviar dados de uma meta de consumo nas respostas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaResponseDTO {

    /**
     * Identificador único da meta.
     */
    private Long id;

    /**
     * Valor de consumo alvo em kWh.
     */
    private Double consumoAlvo;

    /**
     * Data de início da meta.
     */
    private LocalDate dataInicio;

    /**
     * Data de fim da meta.
     */
    private LocalDate dataFim;

    /**
     * Indica se a meta foi atingida.
     */
    private Boolean atingida;

    /**
     * Identificador do usuário proprietário da meta.
     */
    private Long usuarioId;
}
