package br.com.fiap.jadv.prospeco.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <h1>RegistroConsumo</h1>
 * Classe que representa um registro de consumo energético
 * de um determinado aparelho em um momento específico.
 * Utilizada para monitorar e estimar o consumo em tempo real e histórico.
 *
 * <p>
 * Observação: A unidade de medida para o consumo é kWh.
 * </p>
 *
 */
@Entity
@Table(name = "registros_consumo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroConsumo {

    /**
     * Identificador único do registro de consumo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data e hora em que o registro foi feito.
     */
    @NotNull(message = "A data e hora são obrigatórias.")
    private LocalDateTime dataHora;

    /**
     * Valor do consumo em kWh.
     */
    @NotNull(message = "O valor de consumo é obrigatório.")
    @PositiveOrZero(message = "O valor de consumo deve ser positivo ou zero.")
    private Double consumo;

    /**
     * Aparelho ao qual o registro de consumo pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aparelho_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Aparelho aparelho;
}
