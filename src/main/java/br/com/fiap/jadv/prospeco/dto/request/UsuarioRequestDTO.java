package br.com.fiap.jadv.prospeco.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * <h1>UsuarioRequestDTO</h1>
 * DTO utilizado para receber os dados de requisição relacionados ao usuário,
 * como cadastro e atualização de perfil.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    /**
     * Endereço de email único do usuário.
     */
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    /**
     * Senha do usuário.
     */
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;
}