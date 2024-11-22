package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.AparelhoService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aparelhos")
public class AparelhoMvcController {

    private final AparelhoService aparelhoService;
    private final UsuarioService usuarioService;

    @Autowired
    public AparelhoMvcController(AparelhoService aparelhoService, UsuarioService usuarioService) {
        this.aparelhoService = aparelhoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String redirecionarAparelhosUsuario(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/aparelhos/usuario/" + usuarioId;
    }

    @GetMapping("/usuario/{usuarioId}")
    public String listarAparelhosPorUsuario(@PathVariable Long usuarioId, Model model, Pageable pageable) {
        Page<AparelhoResponseDTO> aparelhos = aparelhoService.listarAparelhosPorUsuario(usuarioId, pageable);
        model.addAttribute("aparelhos", aparelhos);
        model.addAttribute("usuarioId", usuarioId);
        return "aparelhos/list";
    }

    @GetMapping("/novo")
    public String redirecionarFormularioCriarAparelho(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/aparelhos/usuario/" + usuarioId + "/novo";
    }

    @GetMapping("/usuario/{usuarioId}/novo")
    public String mostrarFormularioCriarAparelho(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("aparelho", new AparelhoRequestDTO());
        model.addAttribute("usuarioId", usuarioId);
        return "aparelhos/create";
    }

    @PostMapping("/usuario/{usuarioId}")
    public String criarAparelho(@PathVariable Long usuarioId, @ModelAttribute AparelhoRequestDTO aparelhoRequestDTO) {
        aparelhoService.criarAparelho(aparelhoRequestDTO, usuarioId);
        return "redirect:/aparelhos/usuario/" + usuarioId;
    }

    @GetMapping("/{id}")
    public String verAparelho(@PathVariable Long id, Model model) {
        AparelhoResponseDTO aparelho = aparelhoService.buscarAparelhoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));
        model.addAttribute("aparelho", aparelho);
        return "aparelhos/view";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarAparelho(@PathVariable Long id, Model model) {
        AparelhoResponseDTO aparelho = aparelhoService.buscarAparelhoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));
        model.addAttribute("aparelho", aparelho);
        return "aparelhos/edit";
    }

    @PostMapping("/{id}")
    public String atualizarAparelho(@PathVariable Long id, @ModelAttribute AparelhoRequestDTO aparelhoRequestDTO) {
        aparelhoService.atualizarAparelho(id, aparelhoRequestDTO);
        return "redirect:/aparelhos/" + id;
    }

    @GetMapping("/{id}/excluir")
    public String excluirAparelho(@PathVariable Long id) {
        AparelhoResponseDTO aparelho = aparelhoService.buscarAparelhoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));
        Long usuarioId = aparelho.getUsuarioId();
        aparelhoService.excluirAparelho(id);
        return "redirect:/aparelhos/usuario/" + usuarioId;
    }
}
