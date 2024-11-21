package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.MetaService;
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
@RequestMapping("/metas")
public class MetaMvcController {

    private final MetaService metaService;
    private final UsuarioService usuarioService;

    @Autowired
    public MetaMvcController(MetaService metaService, UsuarioService usuarioService) {
        this.metaService = metaService;
        this.usuarioService = usuarioService;
    }

    /**
     * Lists all metas for a user.
     *
     * @param usuarioId ID of the user.
     * @return Page with the list of metas.
     */
    @GetMapping("/usuario/{usuarioId}")
    public String listarMetasPorUsuario(@PathVariable Long usuarioId,
                                        Pageable pageable,
                                        Model model) {
        Page<MetaResponseDTO> metas = metaService.listarMetasPorUsuario(usuarioId, pageable);
        model.addAttribute("metas", metas);
        model.addAttribute("usuarioId", usuarioId);
        return "metas/list";
    }

    /**
     * Displays the form to create a new meta.
     *
     * @param usuarioId ID of the user.
     * @param model     Model for the view.
     * @return Page of the creation form.
     */
    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormCriarMeta(@PathVariable Long usuarioId, Model model) {
        MetaRequestDTO meta = new MetaRequestDTO();
        meta.setUsuarioId(usuarioId);
        model.addAttribute("meta", meta);
        model.addAttribute("usuarioId", usuarioId);
        return "metas/create";
    }

    /**
     * Processes the form to create a new meta.
     *
     * @param usuarioId  ID of the user.
     * @param requestDTO Data of the meta to be created.
     * @param result     Validation result.
     * @param model      Model for the view.
     * @return Redirect or form page if there are errors.
     */
    @PostMapping("/usuario/{usuarioId}")
    public String criarMeta(@PathVariable Long usuarioId,
                            @Valid @ModelAttribute("meta") MetaRequestDTO requestDTO,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", usuarioId);
            return "metas/create";
        }
        requestDTO.setUsuarioId(usuarioId);
        metaService.criarMeta(requestDTO);
        return "redirect:/metas/usuario/" + usuarioId;
    }

    /**
     * Displays the details of a meta.
     *
     * @param id    ID of the meta.
     * @param model Model for the view.
     * @return Page of the meta details.
     */
    @GetMapping("/{id}")
    public String visualizarMeta(@PathVariable Long id, Model model) {
        MetaResponseDTO meta = metaService.buscarMetaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        model.addAttribute("meta", meta);
        return "metas/view";
    }

    /**
     * Displays the form to edit a meta.
     *
     * @param id    ID of the meta.
     * @param model Model for the view.
     * @return Page of the edit form.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarMeta(@PathVariable Long id, Model model) {
        MetaResponseDTO meta = metaService.buscarMetaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        model.addAttribute("meta", meta);
        return "metas/edit";
    }

    /**
     * Processes the form to update a meta.
     *
     * @param id         ID of the meta.
     * @param requestDTO Updated data of the meta.
     * @param result     Validation result.
     * @param model      Model for the view.
     * @return Redirect or form page if there are errors.
     */
    @PostMapping("/{id}")
    public String atualizarMeta(@PathVariable Long id,
                                @Valid @ModelAttribute("meta") MetaRequestDTO requestDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("meta", requestDTO);
            return "metas/edit";
        }
        metaService.atualizarMeta(id, requestDTO);
        return "redirect:/metas/" + id;
    }

    /**
     * Deletes a meta.
     *
     * @param id ID of the meta.
     * @return Redirect to the user's metas list.
     */
    @GetMapping("/{id}/excluir")
    public String excluirMeta(@PathVariable Long id) {
        MetaResponseDTO meta = metaService.buscarMetaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));
        Long usuarioId = meta.getUsuarioId();
        metaService.excluirMeta(id);
        return "redirect:/metas/usuario/" + usuarioId;
    }

    /**
     * Marks a meta as achieved.
     *
     * @param id ID of the meta.
     * @return Redirect to the meta details page.
     */
    @GetMapping("/{id}/atingida")
    public String marcarMetaComoAtingida(@PathVariable Long id) {
        metaService.marcarMetaComoAtingida(id);
        return "redirect:/metas/" + id;
    }
}
