package br.com.fiap.jadv.prospeco.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <h1>Conquista</h1>
 * Classe que representa uma conquista obtida pelo usuário
 * através de ações como atingir metas de economia,
 * uso eficiente de energia, entre outros.
 * Utilizada para gamificação e engajamento do usuário.
 *
 */
@Entity
@Table(name = "conquistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conquista {

    /**
     * Identificador único da conquista.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título da conquista.
     */
    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres.")
    private String titulo;

    /**
     * Descrição detalhada da conquista.
     */
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String descricao;

    /**
     * Data e hora em que a conquista foi obtida.
     */
    @NotNull(message = "A data de conquista é obrigatória.")
    private LocalDateTime dataConquista;

    /**
     * Usuário que obteve a conquista.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;
}
