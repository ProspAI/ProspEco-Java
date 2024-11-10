package br.com.fiap.jadv.prospeco.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

/**
 * <h1>Meta</h1>
 * Classe que representa uma meta de consumo definida pelo usuário.
 * Permite que o usuário estabeleça objetivos de economia de energia
 * em um período específico.
 *
 */
@Entity
@Table(name = "metas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meta {

    /**
     * Identificador único da meta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor de consumo alvo em kWh que o usuário deseja atingir.
     */
    @NotNull(message = "O consumo alvo é obrigatório.")
    @Positive(message = "O consumo alvo deve ser um valor positivo.")
    private Double consumoAlvo;

    /**
     * Data de início da meta.
     */
    @NotNull(message = "A data de início é obrigatória.")
    private LocalDate dataInicio;

    /**
     * Data de fim da meta.
     */
    @NotNull(message = "A data de fim é obrigatória.")
    private LocalDate dataFim;

    /**
     * Indica se a meta foi atingida.
     */
    @NotNull
    private Boolean atingida = false;

    /**
     * Usuário ao qual a meta pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /**
     * Validação personalizada para garantir que a data de fim
     * seja posterior à data de início.
     *
     * @return true se a data de fim for posterior à data de início
     */
    @AssertTrue(message = "A data de fim deve ser posterior à data de início.")
    public boolean isDataFimPosteriorDataInicio() {
        if (dataInicio == null || dataFim == null) {
            return true;
        }
        return dataFim.isAfter(dataInicio);
    }
}
