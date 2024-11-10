package br.com.fiap.jadv.prospeco.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <h1>Notificacao</h1>
 * Classe que representa uma notificação enviada ao usuário.
 * Pode conter alertas sobre bandeiras tarifárias, dicas de economia,
 * lembretes para desligar aparelhos, entre outros.
 *
 */
@Entity
@Table(name = "notificacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    /**
     * Identificador único da notificação.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mensagem da notificação.
     */
    @NotBlank(message = "A mensagem é obrigatória.")
    @Column(columnDefinition = "TEXT")
    private String mensagem;

    /**
     * Data e hora em que a notificação foi enviada.
     */
    @NotNull(message = "A data e hora são obrigatórias.")
    private LocalDateTime dataHora;

    /**
     * Indica se a notificação foi lida pelo usuário.
     */
    @NotNull
    private Boolean lida = false;

    /**
     * Usuário que recebeu a notificação.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;
}
