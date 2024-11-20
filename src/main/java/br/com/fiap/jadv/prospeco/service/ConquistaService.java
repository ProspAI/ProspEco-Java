package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Conquista;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.ConquistaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConquistaService {

    private final ConquistaRepository conquistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ConquistaService(ConquistaRepository conquistaRepository,
                            UsuarioRepository usuarioRepository,
                            KafkaProducerService kafkaProducerService) {
        this.conquistaRepository = conquistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todas as conquistas de um usuário.
     *
     * @param usuarioId Identificador do usuário.
     * @return Lista de ConquistaResponseDTO.
     */
    public Page<ConquistaResponseDTO> listarConquistasPorUsuario(Long usuarioId, Pageable pageable) {
        return conquistaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::toResponseDTO);
    }


    /**
     * Busca uma conquista pelo ID.
     *
     * @param id Identificador da conquista.
     * @return Optional de ConquistaResponseDTO.
     */
    public Optional<ConquistaResponseDTO> buscarConquistaPorId(Long id) {
        return conquistaRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria uma nova conquista para um usuário.
     *
     * @param requestDTO Dados da conquista.
     * @return ConquistaResponseDTO com os dados da conquista criada.
     */
    public ConquistaResponseDTO criarConquista(ConquistaRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Conquista conquista = new Conquista();
        BeanUtils.copyProperties(requestDTO, conquista);
        conquista.setUsuario(usuario);
        conquista.setDataConquista(LocalDateTime.now());

        Conquista novaConquista = conquistaRepository.save(conquista);

        // Enviar evento ao Kafka
        try {
            ConquistaResponseDTO responseDTO = toResponseDTO(novaConquista);
            kafkaProducerService.sendMessage("conquista-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novaConquista);
    }

    /**
     * Atualiza os dados de uma conquista existente.
     *
     * @param id         Identificador da conquista.
     * @param requestDTO Dados de atualização da conquista.
     * @return ConquistaResponseDTO com os dados atualizados.
     */
    public ConquistaResponseDTO atualizarConquista(Long id, ConquistaRequestDTO requestDTO) {
        Conquista conquista = conquistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));

        BeanUtils.copyProperties(requestDTO, conquista, "id", "usuario", "dataConquista");

        Conquista conquistaAtualizada = conquistaRepository.save(conquista);

        // Enviar evento ao Kafka
        try {
            ConquistaResponseDTO responseDTO = toResponseDTO(conquistaAtualizada);
            kafkaProducerService.sendMessage("conquista-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(conquistaAtualizada);
    }

    /**
     * Exclui uma conquista.
     *
     * @param id Identificador da conquista.
     */
    public void excluirConquista(Long id) {
        Conquista conquista = conquistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));

        conquistaRepository.delete(conquista);

        // Enviar evento ao Kafka
        try {
            ConquistaResponseDTO responseDTO = toResponseDTO(conquista);
            kafkaProducerService.sendMessage("conquista-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte uma entidade Conquista para ConquistaResponseDTO.
     *
     * @param conquista Entidade a ser convertida.
     * @return ConquistaResponseDTO correspondente.
     */
    private ConquistaResponseDTO toResponseDTO(Conquista conquista) {
        ConquistaResponseDTO responseDTO = new ConquistaResponseDTO();
        BeanUtils.copyProperties(conquista, responseDTO);
        responseDTO.setUsuarioId(conquista.getUsuario().getId());
        return responseDTO;
    }
}
