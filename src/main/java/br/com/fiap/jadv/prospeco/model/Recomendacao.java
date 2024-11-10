package br.com.fiap.jadv.prospeco.model;

import br.com.fiap.jadv.prospeco.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <h1>Recomendacao</h1>
 * Classe que representa uma recomendação ou dica gerada pela IA,
 * com o objetivo de ajudar o usuário a economizar energia
 * e melhorar seus hábitos de consumo.
 *
 */
@Entity
@Table(name = "recomendacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recomendacao {

    /**
     * Identificador único da recomendação.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mensagem de recomendação gerada pela IA.
     */
    @NotBlank(message = "A mensagem é obrigatória.")
    @Column(columnDefinition = "TEXT")
    private String mensagem;

    /**
     * Data e hora em que a recomendação foi gerada.
     */
    @NotNull(message = "A data e hora são obrigatórias.")
    private LocalDateTime dataHora;

    /**
     * Usuário ao qual a recomendação foi direcionada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;
}
