package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.kafka.KafkaRecomendacaoProducer;
import br.com.fiap.jadv.prospeco.model.Recomendacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.RecomendacaoRepository;
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
public class RecomendacaoService {

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaRecomendacaoProducer kafkaRecomendacaoProducer;

    @Transactional
    public RecomendacaoResponseDTO criarRecomendacao(Long usuarioId, String mensagem) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Recomendacao recomendacao = Recomendacao.builder()
                .mensagem(mensagem)
                .dataHora(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Recomendacao recomendacaoSalva = recomendacaoRepository.save(recomendacao);

        // Enviar recomendação para o Kafka
        RecomendacaoResponseDTO recomendacaoResponseDTO = mapToRecomendacaoResponseDTO(recomendacaoSalva);
        kafkaRecomendacaoProducer.enviarRecomendacao(recomendacaoResponseDTO);

        return recomendacaoResponseDTO;
    }

    /**
     * Busca todas as recomendações de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable Objeto de paginação.
     * @return Página de DTOs de resposta contendo as recomendações do usuário.
     */
    @Transactional(readOnly = true)
    public Page<RecomendacaoResponseDTO> buscarRecomendacoesPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return recomendacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)
                .map(this::mapToRecomendacaoResponseDTO);
    }

    /**
     * Atualiza uma recomendação específica.
     *
     * @param id ID da recomendação.
     * @param novaMensagem Nova mensagem da recomendação.
     * @return DTO de resposta contendo os dados da recomendação atualizada.
     */
    @Transactional
    public RecomendacaoResponseDTO atualizarRecomendacao(Long id, String novaMensagem) {
        Recomendacao recomendacao = recomendacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada."));

        recomendacao.setMensagem(novaMensagem);
        Recomendacao recomendacaoAtualizada = recomendacaoRepository.save(recomendacao);

        // Enviar atualização para o Kafka
        RecomendacaoResponseDTO recomendacaoResponseDTO = mapToRecomendacaoResponseDTO(recomendacaoAtualizada);
        kafkaRecomendacaoProducer.enviarRecomendacao(recomendacaoResponseDTO);

        return recomendacaoResponseDTO;
    }

    /**
     * Exclui uma recomendação pelo ID.
     *
     * @param id ID da recomendação a ser excluída.
     */
    @Transactional
    public void excluirRecomendacao(Long id) {
        if (!recomendacaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Recomendação não encontrada.");
        }
        recomendacaoRepository.deleteById(id);
    }

    /**
     * Mapeia um objeto Recomendacao para RecomendacaoResponseDTO.
     *
     * @param recomendacao Recomendação a ser mapeada.
     * @return DTO de resposta da recomendação.
     */
    private RecomendacaoResponseDTO mapToRecomendacaoResponseDTO(Recomendacao recomendacao) {
        return RecomendacaoResponseDTO.builder()
                .id(recomendacao.getId())
                .mensagem(recomendacao.getMensagem())
                .dataHora(recomendacao.getDataHora())
                .usuarioId(recomendacao.getUsuario().getId())
                .build();
    }
}
