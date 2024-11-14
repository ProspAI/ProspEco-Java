package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.service.MetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    /**
     * Endpoint para criar uma nova meta para o usuário autenticado.
     *
     * @param metaRequestDTO Dados da nova meta de consumo.
     * @return ResponseEntity contendo o MetaResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<MetaResponseDTO> criarMeta(@Valid @RequestBody MetaRequestDTO metaRequestDTO) {
        MetaResponseDTO responseDTO = metaService.criarMeta(metaRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Endpoint para buscar metas de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de MetaResponseDTO.
     */
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Page<MetaResponseDTO>> buscarMetasPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {

        Page<MetaResponseDTO> metas = metaService.listarMetasPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(metas);
    }

    /**
     * Endpoint para atualizar uma meta existente.
     *
     * @param id             ID da meta a ser atualizada.
     * @param metaRequestDTO Dados de atualização da meta.
     * @return ResponseEntity contendo o MetaResponseDTO atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> atualizarMeta(
            @PathVariable Long id, @Valid @RequestBody MetaRequestDTO metaRequestDTO) {

        MetaResponseDTO responseDTO = metaService.atualizarMeta(id, metaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint para excluir uma meta existente.
     *
     * @param id ID da meta a ser excluída.
     * @return ResponseEntity com status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMeta(@PathVariable Long id) {
        metaService.excluirMeta(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para marcar uma meta como atingida.
     *
     * @param id ID da meta a ser marcada como atingida.
     * @return ResponseEntity com status 204 (No Content).
     */
    @PatchMapping("/{id}/atingida")
    public ResponseEntity<Void> marcarMetaComoAtingida(@PathVariable Long id) {
        metaService.marcarMetaComoAtingida(id);
        return ResponseEntity.noContent().build();
    }
}
