package br.com.fiap.jadv.prospeco.dto.response;

import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * <h1>BandeiraTarifariaResponseDTO</h1>
 * DTO utilizado para enviar os dados da bandeira tarifária nas respostas,
 * incluindo o tipo da bandeira e a data de vigência.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandeiraTarifariaResponseDTO {

    /**
     * Identificador único da bandeira tarifária.
     */
    private Long id;

    /**
     * Tipo da bandeira tarifária (Verde, Amarela, Vermelha 1 ou Vermelha 2).
     */
    private TipoBandeira tipoBandeira;

    /**
     * Data de vigência da bandeira tarifária.
     */
    private LocalDate dataVigencia;
}
