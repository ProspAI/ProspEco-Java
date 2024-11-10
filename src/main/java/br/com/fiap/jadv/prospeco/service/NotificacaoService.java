package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.model.Notificacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.NotificacaoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>NotificacaoService</h1>
 * Classe de serviço responsável pela gestão das notificações dos usuários,
 * incluindo criação, consulta e atualização do status de leitura.
 */
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cria uma nova notificação para um usuário específico.
     *
     * @param notificacaoRequestDTO DTO contendo os dados da notificação.
     * @return DTO de resposta contendo os dados da notificação criada.
     */
    @Transactional
    public NotificacaoResponseDTO criarNotificacao(NotificacaoRequestDTO notificacaoRequestDTO) {
        Usuario usuario = usuarioRepository.findById(notificacaoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Notificacao notificacao = Notificacao.builder()
                .mensagem(notificacaoRequestDTO.getMensagem())
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        Notificacao notificacaoSalva = notificacaoRepository.save(notificacao);
        return mapToNotificacaoResponseDTO(notificacaoSalva);
    }

    /**
     * Busca todas as notificações de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de DTOs de resposta contendo as notificações do usuário.
     */
    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarNotificacoesPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario).stream()
                .map(this::mapToNotificacaoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação.
     */
    @Transactional
    public void marcarNotificacaoComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada."));

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    /**
     * Conta o número de notificações não lidas de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Número de notificações não lidas.
     */
    @Transactional(readOnly = true)
    public long contarNotificacoesNaoLidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return notificacaoRepository.countByUsuarioAndLida(usuario, false);
    }

    /**
     * Mapeia um objeto Notificacao para NotificacaoResponseDTO.
     *
     * @param notificacao Notificação a ser mapeada.
     * @return DTO de resposta da notificação.
     */
    private NotificacaoResponseDTO mapToNotificacaoResponseDTO(Notificacao notificacao) {
        return NotificacaoResponseDTO.builder()
                .id(notificacao.getId())
                .mensagem(notificacao.getMensagem())
                .dataHora(notificacao.getDataHora())
                .lida(notificacao.getLida())
                .usuarioId(notificacao.getUsuario().getId())
                .build();
    }
}
