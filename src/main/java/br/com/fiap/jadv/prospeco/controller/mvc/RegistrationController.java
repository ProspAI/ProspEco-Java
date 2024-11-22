package br.com.fiap.jadv.prospeco.controller.mvc;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registerUser(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO responseDTO = usuarioService.criarUsuario(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
