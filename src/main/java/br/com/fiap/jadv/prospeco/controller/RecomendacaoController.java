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

/**
 * <h1>RecomendacaoController</h1>
 * Controller responsável pelos endpoints relacionados às recomendações de consumo dos usuários.
 */
@RestController
@RequestMapping("/recomendacoes")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    /**
     * Cria uma nova recomendação para um usuário específico.
     *
     * @param recomendacaoRequestDTO DTO contendo os dados da recomendação.
     * @return DTO de resposta contendo os dados da recomendação criada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecomendacaoResponseDTO> criarRecomendacao(
            @Valid @RequestBody RecomendacaoRequestDTO recomendacaoRequestDTO) {
        RecomendacaoResponseDTO responseDTO = recomendacaoService.criarRecomendacao(
                recomendacaoRequestDTO.getUsuarioId(),
                recomendacaoRequestDTO.getMensagem()
        );
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Busca todas as recomendações de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Objeto de paginação.
     * @return Página de DTOs de resposta contendo as recomendações do usuário.
     */
    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<RecomendacaoResponseDTO>> buscarRecomendacoesPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        Page<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.buscarRecomendacoesPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(recomendacoes);
    }

    /**
     * Atualiza uma recomendação específica.
     *
     * @param id                      ID da recomendação.
     * @param recomendacaoRequestDTO  DTO contendo a nova mensagem da recomendação.
     * @return DTO de resposta contendo os dados da recomendação atualizada.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecomendacaoResponseDTO> atualizarRecomendacao(
            @PathVariable Long id,
            @Valid @RequestBody RecomendacaoRequestDTO recomendacaoRequestDTO) {
        RecomendacaoResponseDTO responseDTO = recomendacaoService.atualizarRecomendacao(
                id,
                recomendacaoRequestDTO.getMensagem()
        );
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Exclui uma recomendação pelo ID.
     *
     * @param id ID da recomendação a ser excluída.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirRecomendacao(@PathVariable Long id) {
        recomendacaoService.excluirRecomendacao(id);
        return ResponseEntity.noContent().build();
    }
}
