package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.NotificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>NotificacaoController</h1>
 * Controller responsável pelos endpoints relacionados às notificações dos usuários.
 */
@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    /**
     * Cria uma nova notificação para um usuário específico.
     *
     * @param notificacaoRequestDTO DTO contendo os dados da notificação.
     * @return DTO de resposta contendo os dados da notificação criada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificacaoResponseDTO> criarNotificacao(
            @Valid @RequestBody NotificacaoRequestDTO notificacaoRequestDTO) {
        NotificacaoResponseDTO responseDTO = notificacaoService.criarNotificacao(notificacaoRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Busca todas as notificações de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de DTOs de resposta contendo as notificações do usuário.
     */
    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarNotificacoesPorUsuario(@PathVariable Long usuarioId) {
        List<NotificacaoResponseDTO> notificacoes = notificacaoService.buscarNotificacoesPorUsuario(usuarioId);
        return ResponseEntity.ok(notificacoes);
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação.
     * @return Resposta sem conteúdo.
     */
    @PutMapping("/{id}/marcar-como-lida")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> marcarNotificacaoComoLida(@PathVariable Long id) {
        notificacaoService.marcarNotificacaoComoLida(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Conta o número de notificações não lidas de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Número de notificações não lidas.
     */
    @GetMapping("/usuarios/{usuarioId}/nao-lidas")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> contarNotificacoesNaoLidas(@PathVariable Long usuarioId) {
        long count = notificacaoService.contarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(count);
    }
}
