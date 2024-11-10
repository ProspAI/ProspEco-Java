package br.com.fiap.jadv.prospeco.dto.response;

import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import lombok.*;

/**
 * <h1>LoginResponseDTO</h1>
 * DTO para enviar dados de resposta após o login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    /**
     * Token JWT para autenticação nas próximas requisições.
     */
    private String token;

    /**
     * Tipo do token (geralmente "Bearer").
     */
    private String tipo;

    /**
     * Informações básicas do usuário logado.
     */
    private UsuarioResponseDTO usuario;
}
