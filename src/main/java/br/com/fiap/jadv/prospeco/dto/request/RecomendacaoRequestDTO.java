package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * <h1>RecomendacaoRequestDTO</h1>
 * DTO para receber dados de uma nova recomendação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecomendacaoRequestDTO {

    /**
     * Mensagem da recomendação.
     */
    @NotBlank(message = "A mensagem é obrigatória.")
    private String mensagem;

    /**
     * Identificador do usuário que receberá a recomendação.
     */
    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;
}
