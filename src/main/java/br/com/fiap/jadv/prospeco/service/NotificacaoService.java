package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Notificacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.NotificacaoRepository;
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
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaTemplate<String, NotificacaoResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.notificacao-events}")
    private String notificacaoEventsTopic;

    /**
     * Cria uma nova notificação para um usuário específico e envia um evento ao Kafka.
     *
     * @param requestDTO Dados da nova notificação.
     * @return NotificacaoResponseDTO com os dados da notificação criada.
     */
    @Transactional
    public NotificacaoResponseDTO criarNotificacao(NotificacaoRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Notificacao notificacao = Notificacao.builder()
                .mensagem(requestDTO.getMensagem())
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        notificacaoRepository.save(notificacao);

        NotificacaoResponseDTO response = convertToResponseDTO(notificacao);
        kafkaTemplate.send(notificacaoEventsTopic, response);

        return response;
    }

    /**
     * Lista as notificações de um usuário específico com suporte a paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de NotificacaoResponseDTO.
     */
    public Page<NotificacaoResponseDTO> listarNotificacoesPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Conta o número de notificações não lidas de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Número de notificações não lidas.
     */
    public long contarNotificacoesNaoLidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return notificacaoRepository.countByUsuarioAndLida(usuario, false);
    }

    /**
     * Marca uma notificação como lida e envia um evento ao Kafka.
     *
     * @param id ID da notificação a ser marcada como lida.
     */
    @Transactional
    public void marcarNotificacaoComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);

        NotificacaoResponseDTO response = convertToResponseDTO(notificacao);
        kafkaTemplate.send(notificacaoEventsTopic, response);
    }

    /**
     * Converte uma entidade Notificacao para NotificacaoResponseDTO.
     *
     * @param notificacao Entidade Notificacao a ser convertida.
     * @return NotificacaoResponseDTO correspondente.
     */
    private NotificacaoResponseDTO convertToResponseDTO(Notificacao notificacao) {
        return NotificacaoResponseDTO.builder()
                .id(notificacao.getId())
                .mensagem(notificacao.getMensagem())
                .dataHora(notificacao.getDataHora())
                .lida(notificacao.getLida())
                .usuarioId(notificacao.getUsuario().getId())
                .build();
    }
}
