package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Notificacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.NotificacaoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository,
                              KafkaProducerService kafkaProducerService) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Lista todas as notificações de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de NotificacaoResponseDTO.
     */
    public Page<NotificacaoResponseDTO> listarNotificacoesPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Busca uma notificação pelo ID.
     *
     * @param id ID da notificação.
     * @return Optional de NotificacaoResponseDTO.
     */
    public Optional<NotificacaoResponseDTO> buscarNotificacaoPorId(Long id) {
        return notificacaoRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * Cria uma nova notificação para um usuário.
     *
     * @param requestDTO Dados da notificação.
     * @return NotificacaoResponseDTO com os dados da notificação criada.
     */
    public NotificacaoResponseDTO criarNotificacao(NotificacaoRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Notificacao notificacao = new Notificacao();
        BeanUtils.copyProperties(requestDTO, notificacao);
        notificacao.setUsuario(usuario);
        notificacao.setDataHora(LocalDateTime.now());
        notificacao.setLida(false);

        Notificacao novaNotificacao = notificacaoRepository.save(notificacao);

        // Enviar evento ao Kafka
        try {
            NotificacaoResponseDTO responseDTO = toResponseDTO(novaNotificacao);
            kafkaProducerService.sendMessage("notificacao-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novaNotificacao);
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação.
     */
    public void marcarNotificacaoComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        notificacao.setLida(true);
        Notificacao notificacaoLida = notificacaoRepository.save(notificacao);

        // Enviar evento ao Kafka
        try {
            NotificacaoResponseDTO responseDTO = toResponseDTO(notificacaoLida);
            kafkaProducerService.sendMessage("notificacao-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui uma notificação.
     *
     * @param id ID da notificação.
     */
    public void excluirNotificacao(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        notificacaoRepository.delete(notificacao);

        // Enviar evento ao Kafka
        try {
            NotificacaoResponseDTO responseDTO = toResponseDTO(notificacao);
            kafkaProducerService.sendMessage("notificacao-events", responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * Converte uma entidade Notificacao para NotificacaoResponseDTO.
     *
     * @param notificacao Entidade a ser convertida.
     * @return NotificacaoResponseDTO correspondente.
     */
    private NotificacaoResponseDTO toResponseDTO(Notificacao notificacao) {
        NotificacaoResponseDTO responseDTO = new NotificacaoResponseDTO();
        BeanUtils.copyProperties(notificacao, responseDTO);
        responseDTO.setUsuarioId(notificacao.getUsuario().getId());
        return responseDTO;
    }
}
