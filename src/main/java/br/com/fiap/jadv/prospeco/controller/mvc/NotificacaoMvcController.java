package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.NotificacaoService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping
    public String redirecionarNotificacoesUsuario(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }

    @GetMapping("/usuario/{usuarioId}")
    public String listarNotificacoesPorUsuario(@PathVariable Long usuarioId, Pageable pageable, Model model) {
        Page<NotificacaoResponseDTO> notificacoes = notificacaoService.listarNotificacoesPorUsuario(usuarioId, pageable);
        model.addAttribute("notificacoes", notificacoes);
        model.addAttribute("usuarioId", usuarioId);
        return "notificacoes/list";
    }

    @GetMapping("/{id}")
    public String visualizarNotificacao(@PathVariable Long id, Model model) {
        NotificacaoResponseDTO notificacao = notificacaoService.buscarNotificacaoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        model.addAttribute("notificacao", notificacao);
        return "notificacoes/view";
    }

    @GetMapping("/{id}/excluir")
    public String excluirNotificacao(@PathVariable Long id) {
        NotificacaoResponseDTO notificacao = notificacaoService.buscarNotificacaoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        Long usuarioId = notificacao.getUsuarioId();
        notificacaoService.excluirNotificacao(id);
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }

    @GetMapping("/{id}/lida")
    public String marcarNotificacaoComoLida(@PathVariable Long id) {
        NotificacaoResponseDTO notificacao = notificacaoService.buscarNotificacaoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        Long usuarioId = notificacao.getUsuarioId();
        notificacaoService.marcarNotificacaoComoLida(id);
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }

    @GetMapping("/nova")
    public String redirecionarFormularioCriarNotificacao(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/notificacoes/usuario/" + usuarioId + "/nova";
    }

    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormCriarNotificacao(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("notificacao", new NotificacaoRequestDTO());
        model.addAttribute("usuarioId", usuarioId);
        return "notificacoes/create";
    }

    @PostMapping("/usuario/{usuarioId}")
    public String criarNotificacao(@PathVariable Long usuarioId, @ModelAttribute NotificacaoRequestDTO notificacaoRequestDTO) {
        notificacaoRequestDTO.setUsuarioId(usuarioId);
        notificacaoService.criarNotificacao(notificacaoRequestDTO);
        return "redirect:/notificacoes/usuario/" + usuarioId;
    }
}
