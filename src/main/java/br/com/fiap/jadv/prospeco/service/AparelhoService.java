package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaAparelhoProducer;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AparelhoService {

    private final AparelhoRepository aparelhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaAparelhoProducer kafkaAparelhoProducer;

    @Transactional
    public AparelhoResponseDTO criarAparelho(Long usuarioId, AparelhoRequestDTO aparelhoRequestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (aparelhoRepository.existsByNomeAndUsuario(aparelhoRequestDTO.getNome(), usuario)) {
            throw new IllegalArgumentException("Um aparelho com este nome já existe para o usuário.");
        }

        Aparelho aparelho = Aparelho.builder()
                .nome(aparelhoRequestDTO.getNome())
                .potencia(aparelhoRequestDTO.getPotencia())
                .tipo(aparelhoRequestDTO.getTipo())
                .descricao(aparelhoRequestDTO.getDescricao())
                .usuario(usuario)
                .build();

        Aparelho aparelhoSalvo = aparelhoRepository.save(aparelho);

        // Enviar evento de criação de aparelho ao Kafka
        AparelhoResponseDTO aparelhoResponseDTO = mapToAparelhoResponseDTO(aparelhoSalvo);
        kafkaAparelhoProducer.enviarAparelho(aparelhoResponseDTO);

        return aparelhoResponseDTO;
    }

    @Transactional
    public AparelhoResponseDTO atualizarAparelho(Long id, AparelhoRequestDTO aparelhoRequestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aparelho não encontrado."));

        aparelho.setNome(aparelhoRequestDTO.getNome());
        aparelho.setPotencia(aparelhoRequestDTO.getPotencia());
        aparelho.setTipo(aparelhoRequestDTO.getTipo());
        aparelho.setDescricao(aparelhoRequestDTO.getDescricao());

        Aparelho aparelhoAtualizado = aparelhoRepository.save(aparelho);

        // Enviar evento de atualização de aparelho ao Kafka
        AparelhoResponseDTO aparelhoResponseDTO = mapToAparelhoResponseDTO(aparelhoAtualizado);
        kafkaAparelhoProducer.enviarAparelho(aparelhoResponseDTO);

        return aparelhoResponseDTO;
    }

    @Transactional
    public void excluirAparelho(Long id) {
        if (!aparelhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Aparelho não encontrado.");
        }
        aparelhoRepository.deleteById(id);

        // Enviar evento de exclusão de aparelho ao Kafka
        AparelhoResponseDTO aparelhoResponseDTO = AparelhoResponseDTO.builder().id(id).build();
        kafkaAparelhoProducer.enviarAparelho(aparelhoResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AparelhoResponseDTO> buscarAparelhosPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return aparelhoRepository.findByUsuario(usuario, pageable)
                .map(this::mapToAparelhoResponseDTO);
    }

    private AparelhoResponseDTO mapToAparelhoResponseDTO(Aparelho aparelho) {
        return AparelhoResponseDTO.builder()
                .id(aparelho.getId())
                .nome(aparelho.getNome())
                .potencia(aparelho.getPotencia())
                .tipo(aparelho.getTipo())
                .descricao(aparelho.getDescricao())
                .usuarioId(aparelho.getUsuario().getId())
                .build();
    }
}
