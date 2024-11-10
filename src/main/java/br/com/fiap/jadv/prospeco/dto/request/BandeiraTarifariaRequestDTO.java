package br.com.fiap.jadv.prospeco.dto.request;

import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * <h1>BandeiraTarifariaRequestDTO</h1>
 * DTO utilizado para receber os dados de requisição para criação ou atualização
 * de uma bandeira tarifária, incluindo o tipo e a data de vigência.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandeiraTarifariaRequestDTO {

    /**
     * Tipo da bandeira tarifária (Verde, Amarela, Vermelha 1 ou Vermelha 2).
     */
    @NotNull(message = "O tipo de bandeira é obrigatório.")
    private TipoBandeira tipoBandeira;

    /**
     * Data de vigência da bandeira tarifária.
     */
    @NotNull(message = "A data de vigência é obrigatória.")
    @FutureOrPresent(message = "A data de vigência deve ser no presente ou futuro.")
    private LocalDate dataVigencia;
}
