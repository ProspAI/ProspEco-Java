package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.MetaService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/metas")
public class MetaMvcController {

    private final MetaService metaService;
    private final UsuarioService usuarioService;

    @Autowired
    public MetaMvcController(MetaService metaService, UsuarioService usuarioService) {
        this.metaService = metaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String redirecionarMetasUsuario(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/metas/usuario/" + usuarioId;
    }

    @GetMapping("/usuario/{usuarioId}")
    public String listarMetasPorUsuario(@PathVariable Long usuarioId, Model model, Pageable pageable) {
        Page<MetaResponseDTO> metas = metaService.listarMetasPorUsuario(usuarioId, pageable);
        model.addAttribute("metas", metas);
        model.addAttribute("usuarioId", usuarioId);
        return "metas/list";
    }

    @GetMapping("/nova")
    public String redirecionarFormularioCriarMeta(Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();
        return "redirect:/metas/usuario/" + usuarioId + "/nova";
    }

    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormularioCriarMeta(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("meta", new MetaRequestDTO());
        model.addAttribute("usuarioId", usuarioId);
        return "metas/create";
    }

    @PostMapping("/usuario/{usuarioId}")
    public String criarMeta(@PathVariable Long usuarioId, @ModelAttribute MetaRequestDTO metaRequestDTO) {
        metaRequestDTO.setUsuarioId(usuarioId);
        metaService.criarMeta(metaRequestDTO);
        return "redirect:/metas/usuario/" + usuarioId;
    }

    @GetMapping("/{id}")
    public String visualizarMeta(@PathVariable Long id, Model model) {
        MetaResponseDTO meta = metaService.buscarMetaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        model.addAttribute("meta", meta);
        return "metas/view";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarMeta(@PathVariable Long id, Model model) {
        MetaResponseDTO meta = metaService.buscarMetaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        model.addAttribute("meta", meta);
        return "metas/edit";
    }

    @PostMapping("/{id}")
    public String atualizarMeta(@PathVariable Long id, @ModelAttribute MetaRequestDTO metaRequestDTO) {
        metaService.atualizarMeta(id, metaRequestDTO);
        return "redirect:/metas/" + id;
    }

    @GetMapping("/{id}/excluir")
    public String excluirMeta(@PathVariable Long id) {
        MetaResponseDTO meta = metaService.buscarMetaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        Long usuarioId = meta.getUsuarioId();
        metaService.excluirMeta(id);
        return "redirect:/metas/usuario/" + usuarioId;
    }

    @GetMapping("/{id}/atingida")
    public String marcarMetaComoAtingida(@PathVariable Long id) {
        metaService.marcarMetaComoAtingida(id);
        return "redirect:/metas/" + id;
    }
}
