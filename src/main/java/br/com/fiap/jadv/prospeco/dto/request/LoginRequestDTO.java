package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * <h1>LoginRequestDTO</h1>
 * DTO para receber as credenciais de login do usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    /**
     * Endereço de email do usuário.
     */
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    /**
     * Senha do usuário.
     */
    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
}
