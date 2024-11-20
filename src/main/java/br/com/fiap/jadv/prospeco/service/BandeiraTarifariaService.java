package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.BandeiraTarifaria;
import br.com.fiap.jadv.prospeco.repository.BandeiraTarifariaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BandeiraTarifariaService {

    private final BandeiraTarifariaRepository bandeiraTarifariaRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public BandeiraTarifariaService(BandeiraTarifariaRepository bandeiraTarifariaRepository,
                                    KafkaProducerService kafkaProducerService) {
        this.bandeiraTarifariaRepository = bandeiraTarifariaRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todas as bandeiras tarifárias.
     *
     * @return Lista de BandeiraTarifariaResponseDTO.
     */
    public List<BandeiraTarifariaResponseDTO> listarTodasBandeiras() {
        return bandeiraTarifariaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma bandeira tarifária pelo ID.
     *
     * @param id Identificador da bandeira tarifária.
     * @return Optional de BandeiraTarifariaResponseDTO.
     */
    public Optional<BandeiraTarifariaResponseDTO> buscarBandeiraPorId(Long id) {
        return bandeiraTarifariaRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria uma nova bandeira tarifária.
     *
     * @param requestDTO Dados da bandeira a ser criada.
     * @return BandeiraTarifariaResponseDTO com os dados da bandeira criada.
     */
    public BandeiraTarifariaResponseDTO criarBandeira(BandeiraTarifariaRequestDTO requestDTO) {
        BandeiraTarifaria bandeira = new BandeiraTarifaria();
        BeanUtils.copyProperties(requestDTO, bandeira);

        BandeiraTarifaria novaBandeira = bandeiraTarifariaRepository.save(bandeira);

        // Enviar evento ao Kafka
        try {
            BandeiraTarifariaResponseDTO responseDTO = toResponseDTO(novaBandeira);
            kafkaProducerService.sendMessage("bandeira-tarifaria-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novaBandeira);
    }

    /**
     * Atualiza os dados de uma bandeira tarifária existente.
     *
     * @param id         Identificador da bandeira tarifária.
     * @param requestDTO Dados de atualização da bandeira.
     * @return BandeiraTarifariaResponseDTO com os dados atualizados.
     */
    public BandeiraTarifariaResponseDTO atualizarBandeira(Long id, BandeiraTarifariaRequestDTO requestDTO) {
        BandeiraTarifaria bandeira = bandeiraTarifariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira tarifária não encontrada"));

        BeanUtils.copyProperties(requestDTO, bandeira, "id");

        BandeiraTarifaria bandeiraAtualizada = bandeiraTarifariaRepository.save(bandeira);

        // Enviar evento ao Kafka
        try {
            BandeiraTarifariaResponseDTO responseDTO = toResponseDTO(bandeiraAtualizada);
            kafkaProducerService.sendMessage("bandeira-tarifaria-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(bandeiraAtualizada);
    }

    /**
     * Exclui uma bandeira tarifária.
     *
     * @param id Identificador da bandeira a ser excluída.
     */
    public void excluirBandeira(Long id) {
        BandeiraTarifaria bandeira = bandeiraTarifariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira tarifária não encontrada"));

        bandeiraTarifariaRepository.delete(bandeira);

        // Enviar evento ao Kafka
        try {
            BandeiraTarifariaResponseDTO responseDTO = toResponseDTO(bandeira);
            kafkaProducerService.sendMessage("bandeira-tarifaria-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte uma entidade BandeiraTarifaria para BandeiraTarifariaResponseDTO.
     *
     * @param bandeira Entidade a ser convertida.
     * @return BandeiraTarifariaResponseDTO correspondente.
     */
    private BandeiraTarifariaResponseDTO toResponseDTO(BandeiraTarifaria bandeira) {
        BandeiraTarifariaResponseDTO responseDTO = new BandeiraTarifariaResponseDTO();
        BeanUtils.copyProperties(bandeira, responseDTO);
        return responseDTO;
    }
}
