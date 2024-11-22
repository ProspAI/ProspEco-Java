package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.ConquistaService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/conquistas")
public class ConquistaMvcController {

    private final ConquistaService conquistaService;
    private final UsuarioService usuarioService;

    @Autowired
    public ConquistaMvcController(ConquistaService conquistaService, UsuarioService usuarioService) {
        this.conquistaService = conquistaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String redirecionarConquistasUsuario(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/conquistas/usuario/" + usuarioId;
    }

    @GetMapping("/usuario/{usuarioId}")
    public String listarConquistasPorUsuario(@PathVariable Long usuarioId, Model model, Pageable pageable) {
        Page<ConquistaResponseDTO> conquistas = conquistaService.listarConquistasPorUsuario(usuarioId, pageable);
        model.addAttribute("conquistas", conquistas);
        model.addAttribute("usuarioId", usuarioId);
        return "conquistas/list";
    }

    @GetMapping("/nova")
    public String redirecionarFormularioCriarConquista(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/conquistas/usuario/" + usuarioId + "/nova";
    }

    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormularioCriarConquista(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("conquista", new ConquistaRequestDTO());
        model.addAttribute("usuarioId", usuarioId);
        return "conquistas/create";
    }

    @PostMapping("/usuario/{usuarioId}")
    public String criarConquista(@PathVariable Long usuarioId, @ModelAttribute ConquistaRequestDTO conquistaRequestDTO) {
        conquistaRequestDTO.setUsuarioId(usuarioId);
        conquistaService.criarConquista(conquistaRequestDTO);
        return "redirect:/conquistas/usuario/" + usuarioId;
    }

    @GetMapping("/{id}")
    public String visualizarConquista(@PathVariable Long id, Model model) {
        ConquistaResponseDTO conquista = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        model.addAttribute("conquista", conquista);
        return "conquistas/view";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarConquista(@PathVariable Long id, Model model) {
        ConquistaResponseDTO conquista = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        model.addAttribute("conquista", conquista);
        return "conquistas/edit";
    }

    @PostMapping("/{id}")
    public String atualizarConquista(@PathVariable Long id, @ModelAttribute ConquistaRequestDTO conquistaRequestDTO) {
        conquistaService.atualizarConquista(id, conquistaRequestDTO);
        return "redirect:/conquistas/" + id;
    }

    @GetMapping("/{id}/excluir")
    public String excluirConquista(@PathVariable Long id) {
        ConquistaResponseDTO conquista = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        Long usuarioId = conquista.getUsuarioId();
        conquistaService.excluirConquista(id);
        return "redirect:/conquistas/usuario/" + usuarioId;
    }
}
