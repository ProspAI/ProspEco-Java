package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioMvcController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioMvcController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos os usuários.
     *
     * @param model Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping
    public String listarTodosUsuarios(Model model) {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuario/list";
    }

    /**
     * Exibe os detalhes de um usuário.
     *
     * @param id    ID do usuário.
     * @param model Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);
        return "usuario/view";
    }

    /**
     * Exibe o formulário para criação de um novo usuário.
     *
     * @param model Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/novo")
    public String mostrarFormCriarUsuario(Model model) {
        UsuarioRequestDTO usuario = new UsuarioRequestDTO();
        model.addAttribute("usuario", usuario);
        return "usuario/create";
    }

    /**
     * Processa o formulário de criação de um novo usuário.
     *
     * @param usuarioDTO Dados do usuário a ser criado.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping
    public String criarUsuario(@Valid @ModelAttribute("usuario") UsuarioRequestDTO usuarioDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "usuario/create";
        }
        usuarioService.criarUsuario(usuarioDTO);
        return "redirect:/usuarios";
    }

    /**
     * Exibe o formulário para editar um usuário existente.
     *
     * @param id    ID do usuário.
     * @param model Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormEditarUsuario(@PathVariable Long id, Model model) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setEmail(usuario.getEmail());
        // A senha não é exibida
        model.addAttribute("usuario", usuarioDTO);
        model.addAttribute("usuarioId", id);
        return "usuario/edit";
    }

    /**
     * Processa o formulário para atualizar um usuário existente.
     *
     * @param id         ID do usuário.
     * @param usuarioDTO Dados atualizados do usuário.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/{id}")
    public String atualizarUsuario(@PathVariable Long id,
                                   @Valid @ModelAttribute("usuario") UsuarioRequestDTO usuarioDTO,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", id);
            return "usuario/edit";
        }
        usuarioService.atualizarUsuario(id, usuarioDTO);
        return "redirect:/usuarios/" + id;
    }

    /**
     * Exclui um usuário.
     *
     * @param id ID do usuário.
     * @return Redirecionamento para a lista de usuários.
     */
    @GetMapping("/{id}/excluir")
    public String excluirUsuario(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return "redirect:/usuarios";
    }
}
