package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaMetaProducer;
import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.MetaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaMetaProducer kafkaMetaProducer;
    private static final Logger logger = LoggerFactory.getLogger(MetaService.class);

    @Transactional
    public MetaResponseDTO criarMeta(MetaRequestDTO metaRequestDTO) {
        Usuario usuario = usuarioRepository.findById(metaRequestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        validarDataInicioFim(metaRequestDTO.getDataInicio(), metaRequestDTO.getDataFim());

        Meta meta = Meta.builder()
                .consumoAlvo(metaRequestDTO.getConsumoAlvo())
                .dataInicio(metaRequestDTO.getDataInicio())
                .dataFim(metaRequestDTO.getDataFim())
                .atingida(false)
                .usuario(usuario)
                .build();

        Meta metaSalva = metaRepository.save(meta);

        MetaResponseDTO metaResponseDTO = mapToMetaResponseDTO(metaSalva);
        kafkaMetaProducer.enviarMeta(metaResponseDTO);
        logger.info("Meta criada: {}", metaResponseDTO);

        return metaResponseDTO;
    }

    @Transactional(readOnly = true)
    public Page<MetaResponseDTO> buscarMetasPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return metaRepository.findByUsuario(usuario, pageable)
                .map(this::mapToMetaResponseDTO);
    }

    @Transactional
    public MetaResponseDTO atualizarMeta(Long id, MetaRequestDTO metaRequestDTO) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada."));

        validarDataInicioFim(metaRequestDTO.getDataInicio(), metaRequestDTO.getDataFim());

        meta.setConsumoAlvo(metaRequestDTO.getConsumoAlvo());
        meta.setDataInicio(metaRequestDTO.getDataInicio());
        meta.setDataFim(metaRequestDTO.getDataFim());

        Meta metaAtualizada = metaRepository.save(meta);

        MetaResponseDTO metaResponseDTO = mapToMetaResponseDTO(metaAtualizada);
        kafkaMetaProducer.enviarMeta(metaResponseDTO);
        logger.info("Meta atualizada: {}", metaResponseDTO);

        return metaResponseDTO;
    }

    @Transactional
    public void excluirMeta(Long id) {
        if (!metaRepository.existsById(id)) {
            throw new EntityNotFoundException("Meta não encontrada.");
        }
        metaRepository.deleteById(id);
        logger.info("Meta excluída: {}", id);
    }

    @Transactional
    public void marcarMetaComoAtingida(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada."));

        meta.setAtingida(true);
        Meta metaAtualizada = metaRepository.save(meta);

        MetaResponseDTO metaResponseDTO = mapToMetaResponseDTO(metaAtualizada);
        kafkaMetaProducer.enviarMeta(metaResponseDTO);
        logger.info("Meta marcada como atingida: {}", metaResponseDTO);
    }

    private void validarDataInicioFim(LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data de fim deve ser posterior à data de início.");
        }
    }

    private MetaResponseDTO mapToMetaResponseDTO(Meta meta) {
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
