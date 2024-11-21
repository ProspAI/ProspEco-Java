package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.RegistroConsumoService;
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
@RequestMapping("/registro-consumo")
public class RegistroConsumoMvcController {

    private final RegistroConsumoService registroConsumoService;
    private final AparelhoService aparelhoService;

    @Autowired
    public RegistroConsumoMvcController(RegistroConsumoService registroConsumoService, AparelhoService aparelhoService) {
        this.registroConsumoService = registroConsumoService;
        this.aparelhoService = aparelhoService;
    }

    /**
     * Lista todos os registros de consumo de um aparelho.
     *
     * @param aparelhoId ID do aparelho.
     * @param pageable   Informações de paginação.
     * @param model      Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/aparelho/{aparelhoId}")
    public String listarRegistrosPorAparelho(@PathVariable Long aparelhoId,
                                             Pageable pageable,
                                             Model model) {
        Page<RegistroConsumoResponseDTO> registros = registroConsumoService.listarRegistrosPorAparelho(aparelhoId, pageable);
        model.addAttribute("registros", registros);
        model.addAttribute("aparelhoId", aparelhoId);
        return "registro-consumo/list";
    }

    /**
     * Exibe o formulário para criação de um novo registro de consumo.
     *
     * @param aparelhoId ID do aparelho.
     * @param model      Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/aparelho/{aparelhoId}/novo")
    public String mostrarFormCriarRegistro(@PathVariable Long aparelhoId, Model model) {
        RegistroConsumoRequestDTO registro = new RegistroConsumoRequestDTO();
        registro.setAparelhoId(aparelhoId);
        model.addAttribute("registro", registro);
        model.addAttribute("aparelhoId", aparelhoId);
        return "registro-consumo/create";
    }

    /**
     * Processa o formulário de criação de um novo registro de consumo.
     *
     * @param aparelhoId  ID do aparelho.
     * @param requestDTO  Dados do registro a ser criado.
     * @param result      Resultado da validação.
     * @param model       Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/aparelho/{aparelhoId}")
    public String criarRegistroConsumo(@PathVariable Long aparelhoId,
                                       @Valid @ModelAttribute("registro") RegistroConsumoRequestDTO requestDTO,
                                       BindingResult result,
                                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("aparelhoId", aparelhoId);
            return "registro-consumo/create";
        }
        requestDTO.setAparelhoId(aparelhoId);
        registroConsumoService.criarRegistroConsumo(requestDTO);
        return "redirect:/registro-consumo/aparelho/" + aparelhoId;
    }

    /**
     * Exibe os detalhes de um registro de consumo.
     *
     * @param id    ID do registro.
     * @param model Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/{id}")
    public String visualizarRegistro(@PathVariable Long id, Model model) {
        RegistroConsumoResponseDTO registro = registroConsumoService.buscarRegistroPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));
        model.addAttribute("registro", registro);
        return "registro-consumo/view";
    }

    /**
     * Exibe o formulário para editar um registro de consumo.
     *
     * @param id    ID do registro.
     * @param model Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarRegistro(@PathVariable Long id, Model model) {
        RegistroConsumoResponseDTO registro = registroConsumoService.buscarRegistroPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));
        RegistroConsumoRequestDTO requestDTO = new RegistroConsumoRequestDTO();
        requestDTO.setDataHora(registro.getDataHora());
        requestDTO.setConsumo(registro.getConsumo());
        requestDTO.setAparelhoId(registro.getAparelhoId());
        model.addAttribute("registro", requestDTO);
        model.addAttribute("registroId", id);
        return "registro-consumo/edit";
    }

    /**
     * Processa o formulário para atualizar um registro de consumo.
     *
     * @param id         ID do registro.
     * @param requestDTO Dados atualizados do registro.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/{id}")
    public String atualizarRegistroConsumo(@PathVariable Long id,
                                           @Valid @ModelAttribute("registro") RegistroConsumoRequestDTO requestDTO,
                                           BindingResult result,
                                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("registroId", id);
            return "registro-consumo/edit";
        }
        registroConsumoService.atualizarRegistroConsumo(id, requestDTO);
        return "redirect:/registro-consumo/" + id;
    }

    /**
     * Exclui um registro de consumo.
     *
     * @param id ID do registro.
     * @return Redirecionamento para a lista de registros do aparelho.
     */
    @GetMapping("/{id}/excluir")
    public String excluirRegistroConsumo(@PathVariable Long id) {
        RegistroConsumoResponseDTO registro = registroConsumoService.buscarRegistroPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));
        Long aparelhoId = registro.getAparelhoId();
        registroConsumoService.excluirRegistroConsumo(id);
        return "redirect:/registro-consumo/aparelho/" + aparelhoId;
    }
}
