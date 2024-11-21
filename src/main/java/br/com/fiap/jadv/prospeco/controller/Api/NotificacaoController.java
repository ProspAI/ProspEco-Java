package br.com.fiap.jadv.prospeco.controller.Api;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.NotificacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @Autowired
    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    /**
     * Lista todas as notificações de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de NotificacaoResponseDTO.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<NotificacaoResponseDTO>> listarNotificacoesPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<NotificacaoResponseDTO> notificacoes = notificacaoService.listarNotificacoesPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(notificacoes);
    }

    /**
     * Busca uma notificação pelo ID.
     *
     * @param id ID da notificação.
     * @return NotificacaoResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoResponseDTO> buscarNotificacaoPorId(@PathVariable Long id) {
        return notificacaoService.buscarNotificacaoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria uma nova notificação para um usuário.
     *
     * @param requestDTO Dados da notificação.
     * @return NotificacaoResponseDTO com os dados da notificação criada.
     */
    @PostMapping
    public ResponseEntity<NotificacaoResponseDTO> criarNotificacao(@Valid @RequestBody NotificacaoRequestDTO requestDTO) {
        NotificacaoResponseDTO notificacao = notificacaoService.criarNotificacao(requestDTO);
        return ResponseEntity.status(201).body(notificacao);
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação.
     * @return Resposta sem conteúdo (204).
     */
    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarNotificacaoComoLida(@PathVariable Long id) {
        notificacaoService.marcarNotificacaoComoLida(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exclui uma notificação pelo ID.
     *
     * @param id ID da notificação.
     * @return Resposta sem conteúdo (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirNotificacao(@PathVariable Long id) {
        notificacaoService.excluirNotificacao(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Conta o número de notificações não lidas de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Número de notificações não lidas.
     */
    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<Long> contarNotificacoesNaoLidas(@PathVariable Long usuarioId) {
        long naoLidas = notificacaoService.contarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(naoLidas);
    }
}
