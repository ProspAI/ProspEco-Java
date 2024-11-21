package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.ConquistaService;
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
@RequestMapping("/conquistas")
public class ConquistaMvcController {

    private final ConquistaService conquistaService;
    private final UsuarioService usuarioService;

    @Autowired
    public ConquistaMvcController(ConquistaService conquistaService, UsuarioService usuarioService) {
        this.conquistaService = conquistaService;
        this.usuarioService = usuarioService;
    }

    /**
     * Lists all achievements for a user.
     *
     * @param usuarioId ID of the user.
     * @return Page with the list of achievements.
     */
    @GetMapping("/usuario/{usuarioId}")
    public String listarConquistasPorUsuario(@PathVariable Long usuarioId,
                                             Pageable pageable,
                                             Model model) {
        Page<ConquistaResponseDTO> conquistas = conquistaService.listarConquistasPorUsuario(usuarioId, pageable);
        model.addAttribute("conquistas", conquistas);
        model.addAttribute("usuarioId", usuarioId);
        return "conquistas/list";
    }

    /**
     * Displays the form to create a new achievement.
     *
     * @param usuarioId ID of the user.
     * @param model     Model for the view.
     * @return Page of the creation form.
     */
    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormCriarConquista(@PathVariable Long usuarioId, Model model) {
        ConquistaRequestDTO conquista = new ConquistaRequestDTO();
        conquista.setUsuarioId(usuarioId);
        model.addAttribute("conquista", conquista);
        model.addAttribute("usuarioId", usuarioId);
        return "conquistas/create";
    }

    /**
     * Processes the form to create a new achievement.
     *
     * @param usuarioId  ID of the user.
     * @param requestDTO Data of the achievement to be created.
     * @param result     Validation result.
     * @param model      Model for the view.
     * @return Redirect or form page if there are errors.
     */
    @PostMapping("/usuario/{usuarioId}")
    public String criarConquista(@PathVariable Long usuarioId,
                                 @Valid @ModelAttribute("conquista") ConquistaRequestDTO requestDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", usuarioId);
            return "conquistas/create";
        }
        requestDTO.setUsuarioId(usuarioId);
        conquistaService.criarConquista(requestDTO);
        return "redirect:/conquistas/usuario/" + usuarioId;
    }

    /**
     * Displays the details of an achievement.
     *
     * @param id    ID of the achievement.
     * @param model Model for the view.
     * @return Page of the achievement details.
     */
    @GetMapping("/{id}")
    public String visualizarConquista(@PathVariable Long id, Model model) {
        ConquistaResponseDTO conquista = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        model.addAttribute("conquista", conquista);
        return "conquistas/view";
    }

    /**
     * Displays the form to edit an achievement.
     *
     * @param id    ID of the achievement.
     * @param model Model for the view.
     * @return Page of the edit form.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarConquista(@PathVariable Long id, Model model) {
        ConquistaResponseDTO conquista = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        model.addAttribute("conquista", conquista);
        return "conquistas/edit";
    }

    /**
     * Processes the form to update an achievement.
     *
     * @param id         ID of the achievement.
     * @param requestDTO Updated data of the achievement.
     * @param result     Validation result.
     * @param model      Model for the view.
     * @return Redirect or form page if there are errors.
     */
    @PostMapping("/{id}")
    public String atualizarConquista(@PathVariable Long id,
                                     @Valid @ModelAttribute("conquista") ConquistaRequestDTO requestDTO,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("conquista", requestDTO);
            return "conquistas/edit";
        }
        conquistaService.atualizarConquista(id, requestDTO);
        ConquistaResponseDTO conquistaAtualizada = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        return "redirect:/conquistas/" + id;
    }

    /**
     * Deletes an achievement.
     *
     * @param id ID of the achievement.
     * @return Redirect to the user's achievements list.
     */
    @GetMapping("/{id}/excluir")
    public String excluirConquista(@PathVariable Long id) {
        ConquistaResponseDTO conquista = conquistaService.buscarConquistaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada"));
        Long usuarioId = conquista.getUsuarioId();
        conquistaService.excluirConquista(id);
        return "redirect:/conquistas/usuario/" + usuarioId;
    }
}
