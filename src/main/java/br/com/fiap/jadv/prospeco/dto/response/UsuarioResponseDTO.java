package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;

/**
 * <h1>UsuarioResponseDTO</h1>
 * DTO utilizado para enviar os dados do usuário nas respostas,
 * excluindo informações sensíveis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    /**
     * Identificador único do usuário.
     */
    private Long id;

    /**
     * Nome completo do usuário.
     */
    private String nome;

    /**
     * Endereço de email do usuário.
     */
    private String email;

    /**
     * Pontuação acumulada de economia de energia do usuário.
     */
    private Double pontuacaoEconomia;
}
