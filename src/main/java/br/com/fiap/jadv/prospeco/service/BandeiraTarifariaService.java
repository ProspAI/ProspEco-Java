package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaBandeiraTarifariaProducer;
import br.com.fiap.jadv.prospeco.model.BandeiraTarifaria;
import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import br.com.fiap.jadv.prospeco.repository.BandeiraTarifariaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandeiraTarifariaService {

    private final BandeiraTarifariaRepository bandeiraTarifariaRepository;
    private final KafkaBandeiraTarifariaProducer kafkaBandeiraTarifariaProducer;

    @Transactional
    public BandeiraTarifariaResponseDTO definirBandeiraTarifaria(LocalDate dataVigencia, TipoBandeira tipoBandeira) {
        Optional<BandeiraTarifaria> bandeiraExistente = bandeiraTarifariaRepository.findByDataVigencia(dataVigencia);

        BandeiraTarifaria bandeiraTarifaria = bandeiraExistente.orElse(BandeiraTarifaria.builder()
                .dataVigencia(dataVigencia)
                .build());

        bandeiraTarifaria.setTipoBandeira(tipoBandeira);
        BandeiraTarifaria bandeiraSalva = bandeiraTarifariaRepository.save(bandeiraTarifaria);

        // Enviar evento de criação ou atualização de bandeira tarifária ao Kafka
        BandeiraTarifariaResponseDTO bandeiraResponseDTO = mapToBandeiraTarifariaResponseDTO(bandeiraSalva);
        kafkaBandeiraTarifariaProducer.enviarBandeiraTarifaria(bandeiraResponseDTO);

        return bandeiraResponseDTO;
    }

    @Transactional(readOnly = true)
    public BandeiraTarifariaResponseDTO buscarBandeiraPorData(LocalDate dataVigencia) {
        BandeiraTarifaria bandeiraTarifaria = bandeiraTarifariaRepository.findByDataVigencia(dataVigencia)
                .orElseThrow(() -> new EntityNotFoundException("Bandeira tarifária não encontrada para a data especificada."));

        return mapToBandeiraTarifariaResponseDTO(bandeiraTarifaria);
    }

    private BandeiraTarifariaResponseDTO mapToBandeiraTarifariaResponseDTO(BandeiraTarifaria bandeiraTarifaria) {
        return BandeiraTarifariaResponseDTO.builder()
                .id(bandeiraTarifaria.getId())
                .dataVigencia(bandeiraTarifaria.getDataVigencia())
                .tipoBandeira(bandeiraTarifaria.getTipoBandeira())
                .build();
    }
}
