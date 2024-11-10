package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * <h1>AparelhoRequestDTO</h1>
 * DTO para receber dados de requisição relacionados a um aparelho.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AparelhoRequestDTO {

    /**
     * Nome do aparelho.
     */
    @NotBlank(message = "O nome do aparelho é obrigatório.")
    @Size(max = 100, message = "O nome do aparelho deve ter no máximo 100 caracteres.")
    private String nome;

    /**
     * Potência nominal do aparelho em Watts.
     */
    @NotNull(message = "A potência é obrigatória.")
    @Positive(message = "A potência deve ser um valor positivo.")
    private Double potencia;

    /**
     * Tipo do aparelho.
     */
    @NotBlank(message = "O tipo de aparelho é obrigatório.")
    @Size(max = 50, message = "O tipo de aparelho deve ter no máximo 50 caracteres.")
    private String tipo;

    /**
     * Descrição adicional sobre o aparelho.
     */
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String descricao;
}
