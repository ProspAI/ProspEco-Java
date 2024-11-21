package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.NotificacaoService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notificacoes")
public class NotificacaoMvcController {

    private final NotificacaoService notificacaoService;
    private final UsuarioService usuarioService;

    @Autowired
    public NotificacaoMvcController(NotificacaoService notificacaoService, UsuarioService usuarioService) {
        this.notificacaoService = notificacaoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todas as notificações de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Página com a lista de notificações.
     */
    @GetMapping("/usuario/{usuarioId}")
    public String listarNotificacoesPorUsuario(@PathVariable Long usuarioId,
                                               Pageable pageable,
                                               Model model) {
        Page<NotificacaoResponseDTO> notificacoes = notificacaoService.listarNotificacoesPorUsuario(usuarioId, pageable);
        model.addAttribute("notificacoes", notificacoes);
        model.addAttribute("usuarioId", usuarioId);
        return "notificacoes/list";
    }

    /**
     * Exibe os detalhes de uma notificação.
     *
     * @param id    ID da notificação.
     * @param model Modelo para a view.
     * @return Página de detalhes da notificação.
     */
    @GetMapping("/{id}")
    public String visualizarNotificacao(@PathVariable Long id, Model model) {
        NotificacaoResponseDTO notificacao = notificacaoService.buscarNotificacaoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        model.addAttribute("notificacao", notificacao);
        return "notificacoes/view";
    }

    /**
     * Exclui uma notificação.
     *
     * @param id ID da notificação.
     * @return Redirecionamento para a lista de notificações do usuário.
     */
    @GetMapping("/{id}/excluir")
    public String excluirNotificacao(@PathVariable Long id) {
        NotificacaoResponseDTO notificacao = notificacaoService.buscarNotificacaoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        Long usuarioId = notificacao.getUsuarioId();
        notificacaoService.excluirNotificacao(id);
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação.
     * @return Redirecionamento para a lista de notificações do usuário.
     */
    @GetMapping("/{id}/lida")
    public String marcarNotificacaoComoLida(@PathVariable Long id) {
        NotificacaoResponseDTO notificacao = notificacaoService.buscarNotificacaoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        Long usuarioId = notificacao.getUsuarioId();
        notificacaoService.marcarNotificacaoComoLida(id);
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }

    /**
     * Exibe o formulário para criação de uma nova notificação.
     *
     * @param usuarioId ID do usuário.
     * @param model     Modelo para a view.
     * @return Página do formulário de criação.
     */
    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormCriarNotificacao(@PathVariable Long usuarioId, Model model) {
        NotificacaoRequestDTO notificacao = new NotificacaoRequestDTO();
        notificacao.setUsuarioId(usuarioId);
        model.addAttribute("notificacao", notificacao);
        model.addAttribute("usuarioId", usuarioId);
        return "notificacoes/create";
    }

    /**
     * Processa o formulário de criação de uma nova notificação.
     *
     * @param usuarioId  ID do usuário.
     * @param requestDTO Dados da notificação a ser criada.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/usuario/{usuarioId}")
    public String criarNotificacao(@PathVariable Long usuarioId,
                                   @Valid @ModelAttribute("notificacao") NotificacaoRequestDTO requestDTO,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", usuarioId);
            return "notificacoes/create";
        }
        requestDTO.setUsuarioId(usuarioId);
        notificacaoService.criarNotificacao(requestDTO);
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }
}
