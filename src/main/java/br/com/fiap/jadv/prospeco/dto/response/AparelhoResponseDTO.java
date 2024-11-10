package br.com.fiap.jadv.prospeco.dto.response;

import lombok.*;

/**
 * <h1>AparelhoResponseDTO</h1>
 * DTO para enviar dados de um aparelho nas respostas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AparelhoResponseDTO {

    /**
     * Identificador único do aparelho.
     */
    private Long id;

    /**
     * Nome do aparelho.
     */
    private String nome;

    /**
     * Potência nominal do aparelho em Watts.
     */
    private Double potencia;

    /**
     * Tipo do aparelho.
     */
    private String tipo;

    /**
     * Descrição adicional sobre o aparelho.
     */
    private String descricao;

    /**
     * Identificador do usuário proprietário do aparelho.
     */
    private Long usuarioId;
}
