package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.MetaRepository;
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
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public MetaService(MetaRepository metaRepository, UsuarioRepository usuarioRepository, KafkaProducerService kafkaProducerService) {
        this.metaRepository = metaRepository;
        this.usuarioRepository = usuarioRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todas as metas de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de MetaResponseDTO.
     */
    public Page<MetaResponseDTO> listarMetasPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return metaRepository.findByUsuario(usuario, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Busca uma meta pelo ID.
     *
     * @param id ID da meta.
     * @return Optional de MetaResponseDTO.
     */
    public Optional<MetaResponseDTO> buscarMetaPorId(Long id) {
        return metaRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria uma nova meta para um usuário.
     *
     * @param requestDTO Dados da meta a ser criada.
     * @return MetaResponseDTO com os dados da meta criada.
     */
    public MetaResponseDTO criarMeta(MetaRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Meta meta = new Meta();
        BeanUtils.copyProperties(requestDTO, meta);
        meta.setUsuario(usuario);
        meta.setAtingida(false);

        Meta novaMeta = metaRepository.save(meta);

        // Enviar evento ao Kafka
        try {
            MetaResponseDTO responseDTO = toResponseDTO(novaMeta);
            kafkaProducerService.sendMessage("meta-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novaMeta);
    }

    /**
     * Atualiza os dados de uma meta existente.
     *
     * @param id         ID da meta.
     * @param requestDTO Dados de atualização da meta.
     * @return MetaResponseDTO com os dados atualizados.
     */
    public MetaResponseDTO atualizarMeta(Long id, MetaRequestDTO requestDTO) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        BeanUtils.copyProperties(requestDTO, meta, "id", "usuario", "atingida");

        Meta metaAtualizada = metaRepository.save(meta);

        // Enviar evento ao Kafka
        try {
            MetaResponseDTO responseDTO = toResponseDTO(metaAtualizada);
            kafkaProducerService.sendMessage("meta-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(metaAtualizada);
    }

    /**
     * Exclui uma meta existente.
     *
     * @param id ID da meta.
     */
    public void excluirMeta(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        metaRepository.delete(meta);

        // Enviar evento ao Kafka
        try {
            MetaResponseDTO responseDTO = toResponseDTO(meta);
            kafkaProducerService.sendMessage("meta-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Marca uma meta como atingida.
     *
     * @param id ID da meta.
     */
    public void marcarMetaComoAtingida(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        meta.setAtingida(true);
        Meta metaAtingida = metaRepository.save(meta);

        // Enviar evento ao Kafka
        try {
            MetaResponseDTO responseDTO = toResponseDTO(metaAtingida);
            kafkaProducerService.sendMessage("meta-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte uma entidade Meta para MetaResponseDTO.
     *
     * @param meta Entidade a ser convertida.
     * @return MetaResponseDTO correspondente.
     */
    private MetaResponseDTO toResponseDTO(Meta meta) {
        MetaResponseDTO responseDTO = new MetaResponseDTO();
        BeanUtils.copyProperties(meta, responseDTO);
        responseDTO.setUsuarioId(meta.getUsuario().getId());
        return responseDTO;
    }
}
