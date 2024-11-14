package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaConquistaProducer;
import br.com.fiap.jadv.prospeco.model.Conquista;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.ConquistaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConquistaService {

    private final ConquistaRepository conquistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaConquistaProducer kafkaConquistaProducer;

    @Transactional
    public ConquistaResponseDTO criarConquista(ConquistaRequestDTO conquistaRequestDTO) {
        Usuario usuario = usuarioRepository.findById(conquistaRequestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Conquista conquista = Conquista.builder()
                .titulo(conquistaRequestDTO.getTitulo())
                .descricao(conquistaRequestDTO.getDescricao())
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Conquista conquistaSalva = conquistaRepository.save(conquista);

        // Enviar evento ao Kafka
        ConquistaResponseDTO conquistaResponseDTO = mapToConquistaResponseDTO(conquistaSalva);
        kafkaConquistaProducer.enviarConquista(conquistaResponseDTO);

        return conquistaResponseDTO;
    }

    @Transactional(readOnly = true)
    public Page<ConquistaResponseDTO> buscarConquistasPorUsuario(Long usuarioId, Pageable pageable) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }

        return conquistaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::mapToConquistaResponseDTO);
    }

    @Transactional
    public ConquistaResponseDTO atualizarConquista(Long id, ConquistaRequestDTO conquistaRequestDTO) {
        Conquista conquista = conquistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada."));

        conquista.setTitulo(conquistaRequestDTO.getTitulo());
        conquista.setDescricao(conquistaRequestDTO.getDescricao());
        // Atualize dataConquista, se aplicável
        Conquista conquistaAtualizada = conquistaRepository.save(conquista);

        // Enviar evento ao Kafka
        ConquistaResponseDTO conquistaResponseDTO = mapToConquistaResponseDTO(conquistaAtualizada);
        kafkaConquistaProducer.enviarConquista(conquistaResponseDTO);

        return conquistaResponseDTO;
    }

    @Transactional
    public void excluirConquista(Long id) {
        if (!conquistaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conquista não encontrada.");
        }
        conquistaRepository.deleteById(id);

        // Enviar evento de exclusão ao Kafka
        ConquistaResponseDTO conquistaResponseDTO = ConquistaResponseDTO.builder().id(id).build();
        kafkaConquistaProducer.enviarConquista(conquistaResponseDTO);
    }

    private ConquistaResponseDTO mapToConquistaResponseDTO(Conquista conquista) {
        return ConquistaResponseDTO.builder()
                .id(conquista.getId())
                .titulo(conquista.getTitulo())
                .descricao(conquista.getDescricao())
                .dataConquista(conquista.getDataConquista())
                .usuarioId(conquista.getUsuario().getId())
                .build();
    }
}