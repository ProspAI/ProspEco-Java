package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.time.LocalDateTime;

/**
 * <h1>RegistroConsumoRequestDTO</h1>
 * DTO para receber dados de um novo registro de consumo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroConsumoRequestDTO {

    @NotNull(message = "A data e hora são obrigatórias.")
    private LocalDateTime dataHora;

    @NotNull(message = "O valor de consumo é obrigatório.")
    @PositiveOrZero(message = "O valor de consumo deve ser positivo ou zero.")
    private Double consumo;

    @NotNull(message = "O ID do aparelho é obrigatório.")
    private Long aparelhoId;

    // Construtor personalizado para inicializar apenas o aparelhoId
    public RegistroConsumoRequestDTO(Long aparelhoId) {
        this.aparelhoId = aparelhoId;
    }
}
