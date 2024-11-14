package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint para criar um novo usuário.
     *
     * @param requestDTO Dados do novo usuário.
     * @return ResponseEntity contendo o UsuarioResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO responseDTO = usuarioService.criarUsuario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Endpoint para buscar um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return ResponseEntity contendo o UsuarioResponseDTO e o status 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO responseDTO = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint para atualizar os dados de um usuário.
     *
     * @param id         ID do usuário a ser atualizado.
     * @param requestDTO Dados de atualização do usuário.
     * @return ResponseEntity contendo o UsuarioResponseDTO atualizado e o status 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO requestDTO) {

        UsuarioResponseDTO responseDTO = usuarioService.atualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint para excluir um usuário pelo ID.
     *
     * @param id ID do usuário a ser excluído.
     * @return ResponseEntity com status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
