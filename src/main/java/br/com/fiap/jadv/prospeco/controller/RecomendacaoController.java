package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RecomendacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recomendacoes")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    @PostMapping
    public ResponseEntity<RecomendacaoResponseDTO> criarRecomendacao(@Valid @RequestBody RecomendacaoRequestDTO requestDTO) {
        RecomendacaoResponseDTO responseDTO = recomendacaoService.criarRecomendacao(requestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Page<RecomendacaoResponseDTO>> listarRecomendacoesPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {
        Page<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.listarRecomendacoesPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(recomendacoes);
    }
}
