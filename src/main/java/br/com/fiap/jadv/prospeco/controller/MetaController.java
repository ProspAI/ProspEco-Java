package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.service.MetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>MetaController</h1>
 * Controller responsável pelos endpoints relacionados às metas de consumo dos usuários.
 */
@RestController
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    /**
     * Cria uma nova meta para um usuário específico.
     *
     * @param usuarioId      ID do usuário.
     * @param metaRequestDTO DTO contendo os dados da meta.
     * @return DTO de resposta contendo os dados da meta criada.
     */
    @PostMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MetaResponseDTO> criarMeta(
            @PathVariable Long usuarioId,
            @Valid @RequestBody MetaRequestDTO metaRequestDTO) {
        MetaResponseDTO responseDTO = metaService.criarMeta(usuarioId, metaRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Busca todas as metas de um usuário específico.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de DTOs de resposta contendo as metas do usuário.
     */
    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MetaResponseDTO>> buscarMetasPorUsuario(@PathVariable Long usuarioId) {
        List<MetaResponseDTO> metas = metaService.buscarMetasPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    /**
     * Atualiza uma meta existente.
     *
     * @param id             ID da meta.
     * @param metaRequestDTO DTO contendo os novos dados da meta.
     * @return DTO de resposta contendo os dados da meta atualizada.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MetaResponseDTO> atualizarMeta(
            @PathVariable Long id,
            @Valid @RequestBody MetaRequestDTO metaRequestDTO) {
        MetaResponseDTO responseDTO = metaService.atualizarMeta(id, metaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Exclui uma meta pelo ID.
     *
     * @param id ID da meta a ser excluída.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluirMeta(@PathVariable Long id) {
        metaService.excluirMeta(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca uma meta como atingida.
     *
     * @param id ID da meta.
     * @return Resposta sem conteúdo.
     */
    @PutMapping("/{id}/atingida")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> marcarMetaComoAtingida(@PathVariable Long id) {
        metaService.marcarMetaComoAtingida(id);
        return ResponseEntity.noContent().build();
    }
}
