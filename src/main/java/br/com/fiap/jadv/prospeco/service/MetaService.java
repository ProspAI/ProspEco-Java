package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.MetaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Validated
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaTemplate<String, MetaResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.meta-events}")
    private String metaEventsTopic;

    /**
     * Cria uma nova meta de consumo para um usuário específico e envia um evento ao Kafka.
     *
     * @param requestDTO Dados da nova meta.
     * @return MetaResponseDTO com os dados da meta criada.
     */
    @Transactional
    public MetaResponseDTO criarMeta(MetaRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Meta meta = Meta.builder()
                .consumoAlvo(requestDTO.getConsumoAlvo())
                .dataInicio(requestDTO.getDataInicio())
                .dataFim(requestDTO.getDataFim())
                .usuario(usuario)
                .atingida(false)
                .build();

        metaRepository.save(meta);

        MetaResponseDTO response = convertToResponseDTO(meta);
        kafkaTemplate.send(metaEventsTopic, response);

        return response;
    }

    /**
     * Atualiza uma meta existente e envia um evento ao Kafka.
     *
     * @param id         ID da meta a ser atualizada.
     * @param requestDTO Dados de atualização da meta.
     * @return MetaResponseDTO com os dados da meta atualizada.
     */
    @Transactional
    public MetaResponseDTO atualizarMeta(Long id, MetaRequestDTO requestDTO) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        meta.setConsumoAlvo(requestDTO.getConsumoAlvo());
        meta.setDataInicio(requestDTO.getDataInicio());
        meta.setDataFim(requestDTO.getDataFim());
        metaRepository.save(meta);

        MetaResponseDTO response = convertToResponseDTO(meta);
        kafkaTemplate.send(metaEventsTopic, response);

        return response;
    }

    /**
     * Lista as metas de um usuário específico com suporte a paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de MetaResponseDTO.
     */
    public Page<MetaResponseDTO> listarMetasPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return metaRepository.findByUsuario(usuario, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Exclui uma meta e envia um evento ao Kafka.
     *
     * @param id ID da meta a ser excluída.
     */
    @Transactional
    public void excluirMeta(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        metaRepository.delete(meta);
        kafkaTemplate.send(metaEventsTopic, convertToResponseDTO(meta));
    }

    /**
     * Marca uma meta como atingida e envia um evento ao Kafka.
     *
     * @param id ID da meta a ser marcada como atingida.
     */
    @Transactional
    public void marcarMetaComoAtingida(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        meta.setAtingida(true);
        metaRepository.save(meta);

        MetaResponseDTO response = convertToResponseDTO(meta);
        kafkaTemplate.send(metaEventsTopic, response);
    }

    /**
     * Converte uma entidade Meta para MetaResponseDTO.
     *
     * @param meta Entidade Meta a ser convertida.
     * @return MetaResponseDTO correspondente.
     */
    private MetaResponseDTO convertToResponseDTO(Meta meta) {
        return MetaResponseDTO.builder()
                .id(meta.getId())
                .consumoAlvo(meta.getConsumoAlvo())
                .dataInicio(meta.getDataInicio())
                .dataFim(meta.getDataFim())
                .atingida(meta.getAtingida())
                .usuarioId(meta.getUsuario().getId())
                .build();
    }
}
