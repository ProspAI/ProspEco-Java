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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aparelhos")
@RequiredArgsConstructor
public class AparelhoController {

    private final AparelhoService aparelhoService;

    @PostMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AparelhoResponseDTO> criarAparelho(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AparelhoRequestDTO aparelhoRequestDTO) {
        AparelhoResponseDTO responseDTO = aparelhoService.criarAparelho(usuarioId, aparelhoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<AparelhoResponseDTO>> buscarAparelhosPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {
        Page<AparelhoResponseDTO> aparelhos = aparelhoService.buscarAparelhosPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(aparelhos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AparelhoResponseDTO> atualizarAparelho(
            @PathVariable Long id,
            @Valid @RequestBody AparelhoRequestDTO aparelhoRequestDTO) {
        AparelhoResponseDTO responseDTO = aparelhoService.atualizarAparelho(id, aparelhoRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluirAparelho(@PathVariable Long id) {
        aparelhoService.excluirAparelho(id);
        return ResponseEntity.noContent().build();
    }
}
