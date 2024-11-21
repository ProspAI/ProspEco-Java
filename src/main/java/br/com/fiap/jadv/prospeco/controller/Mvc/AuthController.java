package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLoginForm(@RequestParam(value = "error", required = false) String error,
                                   Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenciais inválidas.");
        }
        return "auth/login";
    }

    // Remova ou comente o método processarLogin
    // @PostMapping("/login")
    // public String processarLogin(@RequestParam String email,
    //                              @RequestParam String senha,
    //                              Model model) {
    //     // Este método não é necessário
    // }

    @GetMapping("/register")
    public String mostrarRegistroForm(Model model) {
        model.addAttribute("usuario", new UsuarioRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processarRegistro(@Valid @ModelAttribute("usuario") UsuarioRequestDTO usuarioDTO,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            usuarioService.criarUsuario(usuarioDTO);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao registrar usuário: " + e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, null);
        return "redirect:/login?logout";
    }
}
