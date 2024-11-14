package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.BandeiraTarifaria;
import br.com.fiap.jadv.prospeco.repository.BandeiraTarifariaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class BandeiraTarifariaService {

    private final BandeiraTarifariaRepository bandeiraTarifariaRepository;
    private final KafkaTemplate<String, BandeiraTarifariaResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.bandeira-tarifaria-events}")
    private String bandeiraEventsTopic;

    /**
     * Cria uma nova bandeira tarifária.
     *
     * @param requestDTO Dados da bandeira tarifária.
     * @return BandeiraTarifariaResponseDTO contendo os dados da bandeira criada.
     */
    @Transactional
    public BandeiraTarifariaResponseDTO criarBandeira(BandeiraTarifariaRequestDTO requestDTO) {
        BandeiraTarifaria bandeira = BandeiraTarifaria.builder()
                .tipoBandeira(requestDTO.getTipoBandeira())
                .dataVigencia(requestDTO.getDataVigencia())
                .build();

        bandeiraTarifariaRepository.save(bandeira);

        BandeiraTarifariaResponseDTO response = convertToResponseDTO(bandeira);
        kafkaTemplate.send(bandeiraEventsTopic, response);

        return response;
    }

    /**
     * Atualiza uma bandeira tarifária existente.
     *
     * @param id         Identificador da bandeira a ser atualizada.
     * @param requestDTO Dados de atualização da bandeira.
     * @return BandeiraTarifariaResponseDTO com os dados da bandeira atualizada.
     */
    @Transactional
    public BandeiraTarifariaResponseDTO atualizarBandeira(Long id, BandeiraTarifariaRequestDTO requestDTO) {
        BandeiraTarifaria bandeira = bandeiraTarifariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira tarifária não encontrada"));

        BeanUtils.copyProperties(requestDTO, bandeira, "id");

        bandeiraTarifariaRepository.save(bandeira);

        BandeiraTarifariaResponseDTO response = convertToResponseDTO(bandeira);
        kafkaTemplate.send(bandeiraEventsTopic, response);

        return response;
    }

    /**
     * Obtém a bandeira tarifária para uma data específica.
     *
     * @param dataVigencia Data para a qual buscar a bandeira.
     * @return BandeiraTarifariaResponseDTO com os dados da bandeira encontrada.
     */
    public Optional<BandeiraTarifariaResponseDTO> obterBandeiraPorData(LocalDate dataVigencia) {
        return bandeiraTarifariaRepository.findByDataVigencia(dataVigencia)
                .map(this::convertToResponseDTO);
    }

    /**
     * Converte uma BandeiraTarifaria para BandeiraTarifariaResponseDTO.
     *
     * @param bandeira Bandeira a ser convertida.
     * @return BandeiraTarifariaResponseDTO correspondente.
     */
    private BandeiraTarifariaResponseDTO convertToResponseDTO(BandeiraTarifaria bandeira) {
        return BandeiraTarifariaResponseDTO.builder()
                .id(bandeira.getId())
                .tipoBandeira(bandeira.getTipoBandeira())
                .dataVigencia(bandeira.getDataVigencia())
                .build();
    }
}
