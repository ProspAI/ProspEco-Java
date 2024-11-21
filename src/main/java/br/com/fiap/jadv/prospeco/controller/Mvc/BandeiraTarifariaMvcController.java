package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import br.com.fiap.jadv.prospeco.service.BandeiraTarifariaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bandeiras-tarifarias")
public class BandeiraTarifariaMvcController {

    private final BandeiraTarifariaService bandeiraTarifariaService;

    @Autowired
    public BandeiraTarifariaMvcController(BandeiraTarifariaService bandeiraTarifariaService) {
        this.bandeiraTarifariaService = bandeiraTarifariaService;
    }

    /**
     * Lista todas as bandeiras tarifárias.
     *
     * @return Página com a lista de bandeiras tarifárias.
     */
    @GetMapping
    public String listarBandeiras(Model model) {
        List<BandeiraTarifariaResponseDTO> bandeiras = bandeiraTarifariaService.listarTodasBandeiras();
        model.addAttribute("bandeiras", bandeiras);
        return "bandeiras-tarifarias/list";
    }

    /**
     * Exibe o formulário para criação de uma nova bandeira tarifária.
     *
     * @param model Modelo para a view.
     * @return Página do formulário de criação.
     */
    @GetMapping("/nova")
    public String mostrarFormCriarBandeira(Model model) {
        model.addAttribute("bandeira", new BandeiraTarifariaRequestDTO());
        model.addAttribute("tiposBandeira", TipoBandeira.values());
        return "bandeiras-tarifarias/create";
    }

    /**
     * Processa o formulário de criação de uma nova bandeira tarifária.
     *
     * @param requestDTO Dados da bandeira a ser criada.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping
    public String criarBandeira(@Valid @ModelAttribute("bandeira") BandeiraTarifariaRequestDTO requestDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tiposBandeira", TipoBandeira.values());
            return "bandeiras-tarifarias/create";
        }
        bandeiraTarifariaService.criarBandeira(requestDTO);
        return "redirect:/bandeiras-tarifarias";
    }

    /**
     * Exibe os detalhes de uma bandeira tarifária.
     *
     * @param id    ID da bandeira tarifária.
     * @param model Modelo para a view.
     * @return Página de detalhes da bandeira tarifária.
     */
    @GetMapping("/{id}")
    public String visualizarBandeira(@PathVariable Long id, Model model) {
        BandeiraTarifariaResponseDTO bandeira = bandeiraTarifariaService.buscarBandeiraPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira tarifária não encontrada"));
        model.addAttribute("bandeira", bandeira);
        return "bandeiras-tarifarias/view";
    }

    /**
     * Exibe o formulário para edição de uma bandeira tarifária.
     *
     * @param id    ID da bandeira tarifária.
     * @param model Modelo para a view.
     * @return Página do formulário de edição.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarBandeira(@PathVariable Long id, Model model) {
        BandeiraTarifariaResponseDTO bandeira = bandeiraTarifariaService.buscarBandeiraPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira tarifária não encontrada"));
        model.addAttribute("bandeira", bandeira);
        model.addAttribute("tiposBandeira", TipoBandeira.values());
        return "bandeiras-tarifarias/edit";
    }

    /**
     * Processa o formulário de edição de uma bandeira tarifária.
     *
     * @param id         ID da bandeira tarifária.
     * @param requestDTO Dados atualizados da bandeira.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/{id}")
    public String atualizarBandeira(@PathVariable Long id,
                                    @Valid @ModelAttribute("bandeira") BandeiraTarifariaRequestDTO requestDTO,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tiposBandeira", TipoBandeira.values());
            return "bandeiras-tarifarias/edit";
        }
        bandeiraTarifariaService.atualizarBandeira(id, requestDTO);
        return "redirect:/bandeiras-tarifarias/" + id;
    }

    /**
     * Exclui uma bandeira tarifária.
     *
     * @param id ID da bandeira tarifária.
     * @return Redirecionamento para a lista de bandeiras tarifárias.
     */
    @GetMapping("/{id}/excluir")
    public String excluirBandeira(@PathVariable Long id) {
        bandeiraTarifariaService.excluirBandeira(id);
        return "redirect:/bandeiras-tarifarias";
    }
}
