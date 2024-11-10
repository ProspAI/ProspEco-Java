package br.com.fiap.jadv.prospeco.model;


import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import br.com.fiap.jadv.prospeco.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.HashSet;

/**
 * <h1>Aparelho</h1>
 * Classe que representa um aparelho eletrônico ou eletrodoméstico
 * registrado pelo usuário. Contém informações sobre o aparelho
 * e seus registros de consumo.
 * <p>
 * Utiliza validações para garantir que os dados fornecidos são consistentes
 * e anotações do Lombok para simplificar o código.
 * </p>
 *
 */
@Entity
@Table(name = "aparelhos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aparelho {

    /**
     * Identificador único do aparelho.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
     * Tipo do aparelho (ex: Eletrodoméstico, Eletrônico).
     */
    @NotBlank(message = "O tipo de aparelho é obrigatório.")
    @Size(max = 50, message = "O tipo de aparelho deve ter no máximo 50 caracteres.")
    private String tipo;

    /**
     * Descrição adicional sobre o aparelho.
     */
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String descricao;

    /**
     * Usuário ao qual o aparelho pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /**
     * Lista de registros de consumo associados ao aparelho.
     */
    @OneToMany(mappedBy = "aparelho", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<RegistroConsumo> registrosConsumo = new HashSet<>();
}
