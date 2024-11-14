package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.NotificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    /**
     * Endpoint para criar uma nova notificação para um usuário específico.
     *
     * @param requestDTO Dados da nova notificação.
     * @return ResponseEntity contendo o NotificacaoResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<NotificacaoResponseDTO> criarNotificacao(@Valid @RequestBody NotificacaoRequestDTO requestDTO) {
        NotificacaoResponseDTO responseDTO = notificacaoService.criarNotificacao(requestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Endpoint para listar notificações de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de NotificacaoResponseDTO.
     */
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Page<NotificacaoResponseDTO>> listarNotificacoesPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {

        Page<NotificacaoResponseDTO> notificacoes = notificacaoService.listarNotificacoesPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(notificacoes);
    }

    /**
     * Endpoint para contar notificações não lidas de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Número de notificações não lidas.
     */
    @GetMapping("/usuarios/{usuarioId}/nao-lidas")
    public ResponseEntity<Long> contarNotificacoesNaoLidas(@PathVariable Long usuarioId) {
        long naoLidas = notificacaoService.contarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(naoLidas);
    }

    /**
     * Endpoint para marcar uma notificação como lida.
     *
     * @param id ID da notificação a ser marcada como lida.
     * @return ResponseEntity com status 204 (No Content).
     */
    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarNotificacaoComoLida(@PathVariable Long id) {
        notificacaoService.marcarNotificacaoComoLida(id);
        return ResponseEntity.noContent().build();
    }
}
