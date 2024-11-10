package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.UsuarioRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.UsuarioResponseDTO;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>UsuarioController</h1>
 * Controller responsável pelos endpoints relacionados aos usuários.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Cria um novo usuário no sistema.
     *
     * @param usuarioRequestDTO DTO contendo os dados do usuário a ser criado.
     * @return DTO de resposta contendo os dados do usuário criado.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO responseDTO = usuarioService.criarUsuario(usuarioRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return DTO contendo os dados do usuário encontrado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO responseDTO = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Atualiza as informações de um usuário existente.
     *
     * @param id                 ID do usuário.
     * @param usuarioRequestDTO  DTO contendo os novos dados do usuário.
     * @return DTO de resposta contendo os dados atualizados do usuário.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO responseDTO = usuarioService.atualizarUsuario(id, usuarioRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Exclui um usuário do sistema.
     *
     * @param id ID do usuário a ser excluído.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
