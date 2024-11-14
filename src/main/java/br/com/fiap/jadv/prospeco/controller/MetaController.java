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

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MetaResponseDTO> criarMeta(@Valid @RequestBody MetaRequestDTO metaRequestDTO) {
        MetaResponseDTO responseDTO = metaService.criarMeta(metaRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<MetaResponseDTO>> buscarMetasPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        Page<MetaResponseDTO> metas = metaService.buscarMetasPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MetaResponseDTO> atualizarMeta(
            @PathVariable Long id,
            @Valid @RequestBody MetaRequestDTO metaRequestDTO) {
        MetaResponseDTO responseDTO = metaService.atualizarMeta(id, metaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluirMeta(@PathVariable Long id) {
        metaService.excluirMeta(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/atingida")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> marcarMetaComoAtingida(@PathVariable Long id) {
        metaService.marcarMetaComoAtingida(id);
        return ResponseEntity.noContent().build();
    }
}
