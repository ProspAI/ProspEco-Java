package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.AparelhoService;
import br.com.fiap.jadv.prospeco.service.RegistroConsumoService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
@RequestMapping("/registro-consumo")
public class RegistroConsumoMvcController {

    private final RegistroConsumoService registroConsumoService;
    private final AparelhoService aparelhoService;
    private final UsuarioService usuarioService; // Para obter o ID do usuário

    @Autowired
    public RegistroConsumoMvcController(RegistroConsumoService registroConsumoService,
                                        AparelhoService aparelhoService,
                                        UsuarioService usuarioService) {
        this.registroConsumoService = registroConsumoService;
        this.aparelhoService = aparelhoService;
        this.usuarioService = usuarioService;
    }

    // Redirecionamento para listar aparelhos do usuário logado
    @GetMapping
    public String listarAparelhosDoUsuario(Pageable pageable, Model model) {
        // Obtém o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Obtém o e-mail do usuário autenticado

        // Busca o ID do usuário logado
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(email).getId();

        // Lista os aparelhos do usuário
        Page<AparelhoResponseDTO> aparelhos = aparelhoService.listarAparelhosPorUsuario(usuarioId, pageable);
        model.addAttribute("aparelhos", aparelhos);
        model.addAttribute("usuarioId", usuarioId);

        return "registro-consumo/aparelhos";
    }

    // Listar registros de consumo de um aparelho específico
    @GetMapping("/aparelho/{aparelhoId}")
    public String listarRegistrosPorAparelho(@PathVariable Long aparelhoId, Pageable pageable, Model model) {
        Page<RegistroConsumoResponseDTO> registros = registroConsumoService.listarRegistrosPorAparelho(aparelhoId, pageable);
        model.addAttribute("registros", registros);
        model.addAttribute("aparelhoId", aparelhoId);
        return "registro-consumo/list";
    }

    // Mostrar formulário para criar um novo registro de consumo
    @GetMapping("/aparelho/{aparelhoId}/novo")
    public String mostrarFormCriarRegistro(@PathVariable Long aparelhoId, Model model) {
        model.addAttribute("registro", new RegistroConsumoRequestDTO(aparelhoId));
        model.addAttribute("aparelhoId", aparelhoId);
        return "registro-consumo/create";
    }

    // Criar um novo registro de consumo
    @PostMapping("/aparelho/{aparelhoId}")
    public String criarRegistroConsumo(@PathVariable Long aparelhoId,
                                       @Valid @ModelAttribute("registro") RegistroConsumoRequestDTO requestDTO,
                                       BindingResult result, Model model,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("aparelhoId", aparelhoId);
            return "registro-consumo/create";
        }
        registroConsumoService.criarRegistroConsumo(requestDTO);
        redirectAttributes.addFlashAttribute("sucesso", "Registro de consumo criado com sucesso!");
        return "redirect:/registro-consumo/aparelho/" + aparelhoId;
    }

    // Visualizar detalhes de um registro de consumo
    @GetMapping("/{id}")
    public String visualizarRegistro(@PathVariable Long id, Model model) {
        RegistroConsumoResponseDTO registro = registroConsumoService.buscarRegistroPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));
        model.addAttribute("registro", registro);
        return "registro-consumo/view";
    }

    // Mostrar formulário para editar um registro de consumo
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarRegistro(@PathVariable Long id, Model model) {
        RegistroConsumoResponseDTO registro = registroConsumoService.buscarRegistroPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));
        model.addAttribute("registro", registro);
        return "registro-consumo/edit";
    }

    // Atualizar um registro de consumo
    @PostMapping("/{id}")
    public String atualizarRegistroConsumo(@PathVariable Long id,
                                           @Valid @ModelAttribute("registro") RegistroConsumoRequestDTO requestDTO,
                                           BindingResult result, Model model,
                                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("registroId", id);
            return "registro-consumo/edit";
        }
        RegistroConsumoResponseDTO registroAtualizado = registroConsumoService.atualizarRegistroConsumo(id, requestDTO);
        redirectAttributes.addFlashAttribute("sucesso", "Registro de consumo atualizado com sucesso!");
        return "redirect:/registro-consumo/" + id;
    }

    // Excluir um registro de consumo
    @GetMapping("/{id}/excluir")
    public String excluirRegistroConsumo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long aparelhoId = registroConsumoService.buscarRegistroPorId(id)
                .map(RegistroConsumoResponseDTO::getAparelhoId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de consumo não encontrado"));
        registroConsumoService.excluirRegistroConsumo(id);
        redirectAttributes.addFlashAttribute("sucesso", "Registro de consumo excluído com sucesso!");
        return "redirect:/registro-consumo/aparelho/" + aparelhoId;
    }
}
