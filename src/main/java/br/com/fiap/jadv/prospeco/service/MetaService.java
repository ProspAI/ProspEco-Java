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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaMetaProducer kafkaMetaProducer;

    @Transactional
    public MetaResponseDTO criarMeta(Long usuarioId, MetaRequestDTO metaRequestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Meta meta = Meta.builder()
                .consumoAlvo(metaRequestDTO.getConsumoAlvo())
                .dataInicio(metaRequestDTO.getDataInicio())
                .dataFim(metaRequestDTO.getDataFim())
                .atingida(false)
                .usuario(usuario)
                .build();

        Meta metaSalva = metaRepository.save(meta);

        // Enviar a nova meta para o Kafka
        MetaResponseDTO metaResponseDTO = mapToMetaResponseDTO(metaSalva);
        kafkaMetaProducer.enviarMeta(metaResponseDTO);

        return metaResponseDTO;
    }

    @Transactional
    public MetaResponseDTO atualizarMeta(Long id, MetaRequestDTO metaRequestDTO) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada."));

        meta.setConsumoAlvo(metaRequestDTO.getConsumoAlvo());
        meta.setDataInicio(metaRequestDTO.getDataInicio());
        meta.setDataFim(metaRequestDTO.getDataFim());

        Meta metaAtualizada = metaRepository.save(meta);

        // Enviar a meta atualizada para o Kafka
        MetaResponseDTO metaResponseDTO = mapToMetaResponseDTO(metaAtualizada);
        kafkaMetaProducer.enviarMeta(metaResponseDTO);

        return metaResponseDTO;
    }

    @Transactional
    public void marcarMetaComoAtingida(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada."));

        meta.setAtingida(true);
        Meta metaAtualizada = metaRepository.save(meta);

        // Enviar a meta atingida para o Kafka
        MetaResponseDTO metaResponseDTO = mapToMetaResponseDTO(metaAtualizada);
        kafkaMetaProducer.enviarMeta(metaResponseDTO);
    }


    /**
     * Mapeia um objeto Meta para MetaResponseDTO.
     *
     * @param meta Meta a ser mapeada.
     * @return DTO de resposta da meta.
     */
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
