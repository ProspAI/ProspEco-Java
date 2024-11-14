package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RecomendacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recomendacoes")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    /**
     * Endpoint para criar uma nova recomendação para um usuário específico, gerada pela IA.
     *
     * @param requestDTO Dados da nova recomendação.
     * @return ResponseEntity contendo o RecomendacaoResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<RecomendacaoResponseDTO> criarRecomendacao(@Valid @RequestBody RecomendacaoRequestDTO requestDTO) {
        RecomendacaoResponseDTO responseDTO = recomendacaoService.criarRecomendacao(requestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Endpoint para listar recomendações de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de RecomendacaoResponseDTO.
     */
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Page<RecomendacaoResponseDTO>> listarRecomendacoesPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {

        Page<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.listarRecomendacoesPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(recomendacoes);
    }
}
