package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.RegistroConsumoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistroConsumoService {

    private final RegistroConsumoRepository registroConsumoRepository;
    private final AparelhoRepository aparelhoRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public RegistroConsumoService(RegistroConsumoRepository registroConsumoRepository,
                                  AparelhoRepository aparelhoRepository,
                                  KafkaProducerService kafkaProducerService) {
        this.registroConsumoRepository = registroConsumoRepository;
        this.aparelhoRepository = aparelhoRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todos os registros de consumo de um aparelho.
     *
     * @param aparelhoId ID do aparelho.
     * @return Lista de RegistroConsumoResponseDTO.
     */
    public Page<RegistroConsumoResponseDTO> listarRegistrosPorAparelho(Long aparelhoId, Pageable pageable) {
        Aparelho aparelho = aparelhoRepository.findById(aparelhoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        return registroConsumoRepository.findByAparelho(aparelho, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Busca um registro de consumo pelo ID.
     *
     * @param id ID do registro.
     * @return Optional de RegistroConsumoResponseDTO.
     */
    public Optional<RegistroConsumoResponseDTO> buscarRegistroPorId(Long id) {
        return registroConsumoRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria um novo registro de consumo para um aparelho.
     *
     * @param requestDTO Dados do registro de consumo.
     * @return RegistroConsumoResponseDTO com os dados do registro criado.
     */
    public RegistroConsumoResponseDTO criarRegistroConsumo(RegistroConsumoRequestDTO requestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(requestDTO.getAparelhoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        RegistroConsumo registroConsumo = new RegistroConsumo();
        BeanUtils.copyProperties(requestDTO, registroConsumo);
        registroConsumo.setAparelho(aparelho);

        RegistroConsumo novoRegistro = registroConsumoRepository.save(registroConsumo);

        // Enviar evento ao Kafka
        try {
            RegistroConsumoResponseDTO responseDTO = toResponseDTO(novoRegistro);
            kafkaProducerService.sendMessage("registro-consumo-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novoRegistro);
    }

    /**
     * Atualiza um registro de consumo existente.
     *
     * @param id         ID do registro.
     * @param requestDTO Dados atualizados do registro.
     * @return RegistroConsumoResponseDTO com os dados atualizados.
     */
    public RegistroConsumoResponseDTO atualizarRegistroConsumo(Long id, RegistroConsumoRequestDTO requestDTO) {
        RegistroConsumo registro = registroConsumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));

        BeanUtils.copyProperties(requestDTO, registro, "id", "aparelho");

        RegistroConsumo registroAtualizado = registroConsumoRepository.save(registro);

        // Enviar evento ao Kafka
        try {
            RegistroConsumoResponseDTO responseDTO = toResponseDTO(registroAtualizado);
            kafkaProducerService.sendMessage("registro-consumo-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(registroAtualizado);
    }

    /**
     * Exclui um registro de consumo.
     *
     * @param id ID do registro.
     */
    public void excluirRegistroConsumo(Long id) {
        RegistroConsumo registro = registroConsumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));

        registroConsumoRepository.delete(registro);

        // Enviar evento ao Kafka
        try {
            RegistroConsumoResponseDTO responseDTO = toResponseDTO(registro);
            kafkaProducerService.sendMessage("registro-consumo-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte uma entidade RegistroConsumo para RegistroConsumoResponseDTO.
     *
     * @param registro Entidade a ser convertida.
     * @return RegistroConsumoResponseDTO correspondente.
     */
    private RegistroConsumoResponseDTO toResponseDTO(RegistroConsumo registro) {
        RegistroConsumoResponseDTO responseDTO = new RegistroConsumoResponseDTO();
        BeanUtils.copyProperties(registro, responseDTO);
        responseDTO.setAparelhoId(registro.getAparelho().getId());
        return responseDTO;
    }
}
