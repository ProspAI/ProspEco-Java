package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.AparelhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aparelhos")
public class AparelhoMvcController {

    private final AparelhoService aparelhoService;

    @Autowired
    public AparelhoMvcController(AparelhoService aparelhoService) {
        this.aparelhoService = aparelhoService;
    }

    /**
     * Lista todos os aparelhos de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Página com a lista de aparelhos.
     */
    @GetMapping("/usuario/{usuarioId}")
    public String listarAparelhos(@PathVariable Long usuarioId,
                                  Pageable pageable,
                                  Model model) {
        Page<AparelhoResponseDTO> aparelhos = aparelhoService.listarAparelhosPorUsuario(usuarioId, pageable);
        model.addAttribute("aparelhos", aparelhos);
        model.addAttribute("usuarioId", usuarioId);
        return "aparelhos/list";
    }

    /**
     * Exibe o formulário para criação de um novo aparelho.
     *
     * @param usuarioId ID do usuário.
     * @param model     Modelo para a view.
     * @return Página do formulário de criação.
     */
    @GetMapping("/usuario/{usuarioId}/novo")
    public String mostrarFormCriarAparelho(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("aparelho", new AparelhoRequestDTO());
        model.addAttribute("usuarioId", usuarioId);
        return "aparelhos/create";
    }

    /**
     * Processa o formulário de criação de um novo aparelho.
     *
     * @param usuarioId  ID do usuário.
     * @param requestDTO Dados do aparelho a ser criado.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/usuario/{usuarioId}")
    public String criarAparelho(@PathVariable Long usuarioId,
                                @Valid @ModelAttribute("aparelho") AparelhoRequestDTO requestDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", usuarioId);
            return "aparelhos/create";
        }
        aparelhoService.criarAparelho(requestDTO, usuarioId);
        return "redirect:/aparelhos/usuario/" + usuarioId;
    }

    /**
     * Exibe os detalhes de um aparelho.
     *
     * @param id    ID do aparelho.
     * @param model Modelo para a view.
     * @return Página de detalhes do aparelho.
     */
    @GetMapping("/{id}")
    public String visualizarAparelho(@PathVariable Long id, Model model) {
        AparelhoResponseDTO aparelho = aparelhoService.buscarAparelhoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));
        model.addAttribute("aparelho", aparelho);
        return "aparelhos/view";
    }

    /**
     * Exibe o formulário para edição de um aparelho.
     *
     * @param id    ID do aparelho.
     * @param model Modelo para a view.
     * @return Página do formulário de edição.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarAparelho(@PathVariable Long id, Model model) {
        AparelhoResponseDTO aparelho = aparelhoService.buscarAparelhoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));
        model.addAttribute("aparelho", aparelho);
        return "aparelhos/edit";
    }

    /**
     * Processa o formulário de edição de um aparelho.
     *
     * @param id         ID do aparelho.
     * @param requestDTO Dados do aparelho atualizados.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/{id}")
    public String atualizarAparelho(@PathVariable Long id,
                                    @Valid @ModelAttribute("aparelho") AparelhoRequestDTO requestDTO,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("aparelho", requestDTO);
            return "aparelhos/edit";
        }
        aparelhoService.atualizarAparelho(id, requestDTO);
        return "redirect:/aparelhos/" + id;
    }

    /**
     * Exclui um aparelho.
     *
     * @param id ID do aparelho.
     * @return Redirecionamento para a lista de aparelhos.
     */
    @GetMapping("/{id}/excluir")
    public String excluirAparelho(@PathVariable Long id) {
        AparelhoResponseDTO aparelho = aparelhoService.buscarAparelhoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));
        Long usuarioId = aparelho.getUsuarioId();
        aparelhoService.excluirAparelho(id);
        return "redirect:/aparelhos/usuario/" + usuarioId;
    }
}
