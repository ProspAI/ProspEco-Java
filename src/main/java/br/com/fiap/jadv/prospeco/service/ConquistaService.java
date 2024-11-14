package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Conquista;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.ConquistaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Validated
public class ConquistaService {

    private final ConquistaRepository conquistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaTemplate<String, ConquistaResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.conquista-events}")
    private String conquistaEventsTopic;

    /**
     * Cria uma nova conquista para um usuário específico e envia um evento ao Kafka.
     *
     * @param requestDTO Dados da nova conquista.
     * @return ConquistaResponseDTO com os dados da conquista criada.
     */
    @Transactional
    public ConquistaResponseDTO criarConquista(ConquistaRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Conquista conquista = Conquista.builder()
                .titulo(requestDTO.getTitulo())
                .descricao(requestDTO.getDescricao())
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        conquistaRepository.save(conquista);

        ConquistaResponseDTO response = convertToResponseDTO(conquista);
        kafkaTemplate.send(conquistaEventsTopic, response);

        return response;
    }

    /**
     * Lista conquistas de um usuário específico com suporte a paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de ConquistaResponseDTO.
     */
    public Page<ConquistaResponseDTO> listarConquistasPorUsuario(Long usuarioId, Pageable pageable) {
        return conquistaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Converte uma entidade Conquista para ConquistaResponseDTO.
     *
     * @param conquista Entidade Conquista a ser convertida.
     * @return ConquistaResponseDTO correspondente.
     */
    private ConquistaResponseDTO convertToResponseDTO(Conquista conquista) {
        return ConquistaResponseDTO.builder()
                .id(conquista.getId())
                .titulo(conquista.getTitulo())
                .descricao(conquista.getDescricao())
                .dataConquista(conquista.getDataConquista())
                .usuarioId(conquista.getUsuario().getId())
                .build();
    }
}
