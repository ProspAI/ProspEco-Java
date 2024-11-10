package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.service.AparelhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>AparelhoController</h1>
 * Controller responsável pelos endpoints relacionados aos aparelhos dos usuários.
 */
@RestController
@RequestMapping("/aparelhos")
@RequiredArgsConstructor
public class AparelhoController {

    private final AparelhoService aparelhoService;

    /**
     * Cria um novo aparelho para um usuário específico.
     *
     * @param usuarioId           ID do usuário.
     * @param aparelhoRequestDTO  DTO contendo os dados do aparelho a ser criado.
     * @return DTO de resposta contendo os dados do aparelho criado.
     */
    @PostMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AparelhoResponseDTO> criarAparelho(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AparelhoRequestDTO aparelhoRequestDTO) {
        AparelhoResponseDTO responseDTO = aparelhoService.criarAparelho(usuarioId, aparelhoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Busca todos os aparelhos registrados de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de DTOs de resposta contendo os dados dos aparelhos do usuário.
     */
    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AparelhoResponseDTO>> buscarAparelhosPorUsuario(@PathVariable Long usuarioId) {
        List<AparelhoResponseDTO> aparelhos = aparelhoService.buscarAparelhosPorUsuario(usuarioId);
        return ResponseEntity.ok(aparelhos);
    }

    /**
     * Atualiza os dados de um aparelho específico.
     *
     * @param id                  ID do aparelho.
     * @param aparelhoRequestDTO  DTO contendo os novos dados do aparelho.
     * @return DTO de resposta contendo os dados atualizados do aparelho.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AparelhoResponseDTO> atualizarAparelho(
            @PathVariable Long id,
            @Valid @RequestBody AparelhoRequestDTO aparelhoRequestDTO) {
        AparelhoResponseDTO responseDTO = aparelhoService.atualizarAparelho(id, aparelhoRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Exclui um aparelho pelo ID.
     *
     * @param id ID do aparelho a ser excluído.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluirAparelho(@PathVariable Long id) {
        aparelhoService.excluirAparelho(id);
        return ResponseEntity.noContent().build();
    }
}
