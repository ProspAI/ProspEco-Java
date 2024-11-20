package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AparelhoService {

    private final AparelhoRepository aparelhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public AparelhoService(AparelhoRepository aparelhoRepository,
                           UsuarioRepository usuarioRepository,
                           KafkaProducerService kafkaProducerService) {
        this.aparelhoRepository = aparelhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todos os aparelhos de um usuário.
     *
     * @param usuarioId Identificador do usuário.
     * @return Lista de AparelhoResponseDTO.
     */
    public Page<AparelhoResponseDTO> listarAparelhosPorUsuario(Long usuarioId, Pageable pageable) {
        return aparelhoRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Busca um aparelho por ID.
     *
     * @param id Identificador do aparelho.
     * @return Optional de AparelhoResponseDTO.
     */
    public Optional<AparelhoResponseDTO> buscarAparelhoPorId(Long id) {
        return aparelhoRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria um novo aparelho.
     *
     * @param requestDTO Dados do aparelho a ser criado.
     * @param usuarioId  Identificador do usuário.
     * @return AparelhoResponseDTO com os dados do aparelho criado.
     */
    public AparelhoResponseDTO criarAparelho(AparelhoRequestDTO requestDTO, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Aparelho aparelho = new Aparelho();
        BeanUtils.copyProperties(requestDTO, aparelho);
        aparelho.setUsuario(usuario);

        Aparelho novoAparelho = aparelhoRepository.save(aparelho);

        // Enviar evento ao Kafka
        try {
            AparelhoResponseDTO responseDTO = toResponseDTO(novoAparelho);
            kafkaProducerService.sendMessage("aparelho-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novoAparelho);
    }

    /**
     * Atualiza os dados de um aparelho existente.
     *
     * @param id         Identificador do aparelho.
     * @param requestDTO Dados do aparelho a serem atualizados.
     * @return AparelhoResponseDTO com os dados atualizados.
     */
    public AparelhoResponseDTO atualizarAparelho(Long id, AparelhoRequestDTO requestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        BeanUtils.copyProperties(requestDTO, aparelho, "id", "usuario");

        Aparelho aparelhoAtualizado = aparelhoRepository.save(aparelho);

        // Enviar evento ao Kafka
        try {
            AparelhoResponseDTO responseDTO = toResponseDTO(aparelhoAtualizado);
            kafkaProducerService.sendMessage("aparelho-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(aparelhoAtualizado);
    }

    /**
     * Exclui um aparelho.
     *
     * @param id Identificador do aparelho.
     */
    public void excluirAparelho(Long id) {
        Aparelho aparelho = aparelhoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        aparelhoRepository.delete(aparelho);

        // Enviar evento ao Kafka
        try {
            AparelhoResponseDTO responseDTO = toResponseDTO(aparelho);
            kafkaProducerService.sendMessage("aparelho-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte um Aparelho em AparelhoResponseDTO.
     *
     * @param aparelho Aparelho a ser convertido.
     * @return AparelhoResponseDTO correspondente.
     */
    private AparelhoResponseDTO toResponseDTO(Aparelho aparelho) {
        AparelhoResponseDTO responseDTO = new AparelhoResponseDTO();
        BeanUtils.copyProperties(aparelho, responseDTO);
        responseDTO.setUsuarioId(aparelho.getUsuario().getId());
        return responseDTO;
    }
}
