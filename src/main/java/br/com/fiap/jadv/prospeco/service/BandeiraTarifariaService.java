package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.model.BandeiraTarifaria;
import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import br.com.fiap.jadv.prospeco.repository.BandeiraTarifariaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * <h1>BandeiraTarifariaService</h1>
 * Classe de serviço responsável pela gestão das bandeiras tarifárias, incluindo
 * criação, consulta e atualização das bandeiras vigentes.
 */
@Service
@RequiredArgsConstructor
public class BandeiraTarifariaService {

    private final BandeiraTarifariaRepository bandeiraTarifariaRepository;

    /**
     * Cria ou atualiza a bandeira tarifária para uma data específica.
     *
     * @param dataVigencia Data de vigência da bandeira tarifária.
     * @param tipoBandeira Tipo de bandeira tarifária (Verde, Amarela, Vermelha 1, Vermelha 2).
     * @return DTO de resposta contendo os dados da bandeira tarifária criada ou atualizada.
     */
    @Transactional
    public BandeiraTarifariaResponseDTO definirBandeiraTarifaria(LocalDate dataVigencia, TipoBandeira tipoBandeira) {
        Optional<BandeiraTarifaria> bandeiraExistente = bandeiraTarifariaRepository.findByDataVigencia(dataVigencia);

        BandeiraTarifaria bandeiraTarifaria = bandeiraExistente.orElse(BandeiraTarifaria.builder()
                .dataVigencia(dataVigencia)
                .build());

        bandeiraTarifaria.setTipoBandeira(tipoBandeira);
        BandeiraTarifaria bandeiraSalva = bandeiraTarifariaRepository.save(bandeiraTarifaria);

        return mapToBandeiraTarifariaResponseDTO(bandeiraSalva);
    }

    /**
     * Consulta a bandeira tarifária vigente para uma data específica.
     *
     * @param dataVigencia Data para a qual se deseja consultar a bandeira tarifária.
     * @return DTO de resposta contendo os dados da bandeira tarifária vigente.
     */
    @Transactional(readOnly = true)
    public BandeiraTarifariaResponseDTO buscarBandeiraPorData(LocalDate dataVigencia) {
        BandeiraTarifaria bandeiraTarifaria = bandeiraTarifariaRepository.findByDataVigencia(dataVigencia)
                .orElseThrow(() -> new EntityNotFoundException("Bandeira tarifária não encontrada para a data especificada."));

        return mapToBandeiraTarifariaResponseDTO(bandeiraTarifaria);
    }

    /**
     * Mapeia um objeto BandeiraTarifaria para BandeiraTarifariaResponseDTO.
     *
     * @param bandeiraTarifaria Bandeira tarifária a ser mapeada.
     * @return DTO de resposta da bandeira tarifária.
     */
    private BandeiraTarifariaResponseDTO mapToBandeiraTarifariaResponseDTO(BandeiraTarifaria bandeiraTarifaria) {
        return BandeiraTarifariaResponseDTO.builder()
                .id(bandeiraTarifaria.getId())
                .dataVigencia(bandeiraTarifaria.getDataVigencia())
                .tipoBandeira(bandeiraTarifaria.getTipoBandeira())
                .build();
    }
}
