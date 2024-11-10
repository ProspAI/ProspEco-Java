package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * <h1>NotificacaoRequestDTO</h1>
 * DTO para receber dados de uma nova notificação.
 * Geralmente, notificações são geradas pelo sistema,
 * mas este DTO pode ser útil para testes ou funcionalidades administrativas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoRequestDTO {

    /**
     * Mensagem da notificação.
     */
    @NotBlank(message = "A mensagem é obrigatória.")
    private String mensagem;

    /**
     * Identificador do usuário que receberá a notificação.
     */
    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;
}
