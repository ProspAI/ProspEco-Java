package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RecomendacaoService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recomendacoes")
public class RecomendacaoMvcController {

    private final RecomendacaoService recomendacaoService;
    private final UsuarioService usuarioService;

    @Autowired
    public RecomendacaoMvcController(RecomendacaoService recomendacaoService, UsuarioService usuarioService) {
        this.recomendacaoService = recomendacaoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String redirecionarRecomendacoesUsuario(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/recomendacoes/usuario/" + usuarioId;
    }

    @GetMapping("/usuario/{usuarioId}")
    public String listarRecomendacoesPorUsuario(@PathVariable Long usuarioId, Pageable pageable, Model model) {
        Page<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.listarRecomendacoesPorUsuario(usuarioId, pageable);
        model.addAttribute("recomendacoes", recomendacoes);
        model.addAttribute("usuarioId", usuarioId);
        return "recomendacoes/list";
    }

    @GetMapping("/nova")
    public String redirecionarFormularioCriarRecomendacao(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/recomendacoes/usuario/" + usuarioId + "/nova";
    }

    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormularioCriarRecomendacao(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("recomendacao", new RecomendacaoRequestDTO());
        model.addAttribute("usuarioId", usuarioId);
        return "recomendacoes/create";
    }

    @PostMapping("/usuario/{usuarioId}")
    public String criarRecomendacao(@PathVariable Long usuarioId, @ModelAttribute RecomendacaoRequestDTO recomendacaoRequestDTO) {
        recomendacaoRequestDTO.setUsuarioId(usuarioId);
        recomendacaoService.criarRecomendacao(recomendacaoRequestDTO);
        return "redirect:/recomendacoes/usuario/" + usuarioId;
    }
}
