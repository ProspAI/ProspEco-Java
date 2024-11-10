package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * <h1>ConquistaRequestDTO</h1>
 * DTO para receber dados de uma nova conquista.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConquistaRequestDTO {

    /**
     * Título da conquista.
     */
    @NotBlank(message = "O título é obrigatório.")
    private String titulo;

    /**
     * Descrição da conquista.
     */
    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    /**
     * Identificador do usuário que obteve a conquista.
     */
    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;
}
