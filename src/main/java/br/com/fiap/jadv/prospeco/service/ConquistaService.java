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

    /**
     * Cria uma nova conquista para um usuário.
     *
     * @param conquistaRequestDTO DTO com os dados da nova conquista.
     * @return DTO com os dados da conquista criada.
     */
    @Transactional
    public ConquistaResponseDTO criarConquista(ConquistaRequestDTO conquistaRequestDTO) {
        Usuario usuario = usuarioRepository.findById(conquistaRequestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Conquista conquista = Conquista.builder()
                .titulo(conquistaRequestDTO.getTitulo())
                .descricao(conquistaRequestDTO.getDescricao())
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Conquista conquistaSalva = conquistaRepository.save(conquista);

        // Enviar evento de criação de conquista ao Kafka
        ConquistaResponseDTO conquistaResponseDTO = mapToConquistaResponseDTO(conquistaSalva);
        kafkaConquistaProducer.enviarConquista(conquistaResponseDTO);

        return conquistaResponseDTO;
    }

    /**
     * Busca conquistas de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable Objeto de paginação.
     * @return Página de conquistas do usuário.
     */
    @Transactional(readOnly = true)
    public Page<ConquistaResponseDTO> buscarConquistasPorUsuario(Long usuarioId, Pageable pageable) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        return conquistaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::mapToConquistaResponseDTO);
    }

    /**
     * Atualiza os dados de uma conquista existente.
     *
     * @param id ID da conquista.
     * @param conquistaRequestDTO DTO com os novos dados da conquista.
     * @return DTO com os dados atualizados da conquista.
     */
    @Transactional
    public ConquistaResponseDTO atualizarConquista(Long id, ConquistaRequestDTO conquistaRequestDTO) {
        Conquista conquista = conquistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conquista não encontrada."));

        conquista.setTitulo(conquistaRequestDTO.getTitulo());
        conquista.setDescricao(conquistaRequestDTO.getDescricao());

        Conquista conquistaAtualizada = conquistaRepository.save(conquista);

        // Enviar evento de atualização de conquista ao Kafka
        ConquistaResponseDTO conquistaResponseDTO = mapToConquistaResponseDTO(conquistaAtualizada);
        kafkaConquistaProducer.enviarConquista(conquistaResponseDTO);

        return conquistaResponseDTO;
    }

    /**
     * Exclui uma conquista pelo ID.
     *
     * @param id ID da conquista.
     */
    @Transactional
    public void excluirConquista(Long id) {
        if (!conquistaRepository.existsById(id)) {
            throw new EntityNotFoundException("Conquista não encontrada.");
        }
        conquistaRepository.deleteById(id);

        // Enviar evento de exclusão de conquista ao Kafka
        ConquistaResponseDTO conquistaResponseDTO = ConquistaResponseDTO.builder().id(id).build();
        kafkaConquistaProducer.enviarConquista(conquistaResponseDTO);
    }

    /**
     * Converte uma entidade Conquista para um DTO de resposta.
     *
     * @param conquista Entidade Conquista.
     * @return DTO de resposta.
     */
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
