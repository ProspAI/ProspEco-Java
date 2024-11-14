package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.service.AparelhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aparelhos")
@RequiredArgsConstructor
public class AparelhoController {

    private final AparelhoService aparelhoService;

    /**
     * Endpoint para criar um novo aparelho para um usuário específico.
     *
     * @param usuarioId Identificador do usuário.
     * @param requestDTO Dados do aparelho.
     * @return ResponseEntity contendo o AparelhoResponseDTO e o status 201 (Created).
     */
    @PostMapping("/usuarios/{usuarioId}")
    public ResponseEntity<AparelhoResponseDTO> criarAparelho(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AparelhoRequestDTO requestDTO) {

        AparelhoResponseDTO response = aparelhoService.criarAparelho(requestDTO, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para atualizar um aparelho existente.
     *
     * @param id Identificador do aparelho.
     * @param requestDTO Dados de atualização do aparelho.
     * @return ResponseEntity contendo o AparelhoResponseDTO atualizado e o status 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> atualizarAparelho(
            @PathVariable Long id,
            @Valid @RequestBody AparelhoRequestDTO requestDTO) {

        AparelhoResponseDTO response = aparelhoService.atualizarAparelho(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para listar aparelhos de um usuário específico com paginação.
     *
     * @param usuarioId Identificador do usuário.
     * @param pageable Configuração de paginação.
     * @return Página de AparelhoResponseDTO.
     */
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Page<AparelhoResponseDTO>> listarAparelhosPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {

        Page<AparelhoResponseDTO> aparelhos = aparelhoService.listarAparelhosPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(aparelhos);
    }

    /**
     * Endpoint para excluir um aparelho.
     *
     * @param id Identificador do aparelho a ser excluído.
     * @return ResponseEntity com o status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAparelho(@PathVariable Long id) {
        aparelhoService.excluirAparelho(id);
        return ResponseEntity.noContent().build();
    }
}
