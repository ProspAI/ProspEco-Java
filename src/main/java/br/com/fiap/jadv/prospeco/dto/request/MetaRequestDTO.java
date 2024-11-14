package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

/**
 * <h1>MetaRequestDTO</h1>
 * DTO para receber dados de requisição relacionados a uma meta de consumo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaRequestDTO {

    /**
     * Valor de consumo alvo em kWh.
     */
    @NotNull(message = "O consumo alvo é obrigatório.")
    @Positive(message = "O consumo alvo deve ser um valor positivo.")
    private Double consumoAlvo;

    /**
     * Data de início da meta.
     */
    @NotNull(message = "A data de início é obrigatória.")
    private LocalDate dataInicio;

    /**
     * Data de fim da meta.
     */
    @NotNull(message = "A data de fim é obrigatória.")
    private LocalDate dataFim;

    /**
     * Identificador do usuário que define a meta.
     */
    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;
}
