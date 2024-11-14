package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.config.JwtTokenProvider;
import br.com.fiap.jadv.prospeco.dto.request.LoginRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.LoginResponseDTO;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    /**
     * Endpoint para autenticar o usuário e retornar um token JWT.
     *
     * @param loginRequestDTO DTO contendo email e senha.
     * @return DTO de resposta contendo o token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getSenha()
                )
        );

        String token = tokenProvider.generateToken(authentication.getName());
        LoginResponseDTO responseDTO = LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuarioService.buscarUsuarioPorEmail(authentication.getName()))
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
