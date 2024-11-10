package br.com.fiap.jadv.prospeco.model;

import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * <h1>BandeiraTarifaria</h1>
 * Classe que representa a bandeira tarifária vigente,
 * conforme definido pela ANEEL. As bandeiras tarifárias
 * influenciam o custo da energia elétrica e podem afetar
 * as recomendações de consumo.
 *
 * @see TipoBandeira
 *
 */
@Entity
@Table(name = "bandeiras_tarifarias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandeiraTarifaria {

    /**
     * Identificador único da bandeira tarifária.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo da bandeira tarifária (Verde, Amarela, Vermelha 1 ou Vermelha 2).
     */
    @NotNull(message = "O tipo de bandeira é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoBandeira tipoBandeira;

    /**
     * Data de vigência da bandeira tarifária.
     */
    @NotNull(message = "A data de vigência é obrigatória.")
    private LocalDate dataVigencia;
}
