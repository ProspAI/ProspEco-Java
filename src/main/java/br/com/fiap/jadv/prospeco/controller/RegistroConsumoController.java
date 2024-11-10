package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RegistroConsumoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>RegistroConsumoController</h1>
 * Controller responsável pelos endpoints relacionados aos registros de consumo dos aparelhos.
 */
@RestController
@RequestMapping("/registros-consumo")
@RequiredArgsConstructor
public class RegistroConsumoController {

    private final RegistroConsumoService registroConsumoService;

    /**
     * Cria um novo registro de consumo para um aparelho específico.
     *
     * @param registroConsumoRequestDTO DTO contendo os dados do registro de consumo.
     * @return DTO de resposta contendo os dados do registro de consumo criado.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RegistroConsumoResponseDTO> criarRegistroConsumo(
            @Valid @RequestBody RegistroConsumoRequestDTO registroConsumoRequestDTO) {
        RegistroConsumoResponseDTO responseDTO = registroConsumoService.criarRegistroConsumo(registroConsumoRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Busca os registros de consumo de um aparelho específico, com suporte a paginação.
     *
     * @param aparelhoId ID do aparelho.
     * @param pageable   Objeto de paginação.
     * @return Página de registros de consumo do aparelho.
     */
    @GetMapping("/aparelhos/{aparelhoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<RegistroConsumoResponseDTO>> buscarRegistrosPorAparelho(
            @PathVariable Long aparelhoId,
            Pageable pageable) {
        Page<RegistroConsumoResponseDTO> registros = registroConsumoService.buscarRegistrosPorAparelho(aparelhoId, pageable);
        return ResponseEntity.ok(registros);
    }

    /**
     * Exclui um registro de consumo pelo ID.
     *
     * @param id ID do registro de consumo a ser excluído.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluirRegistroConsumo(@PathVariable Long id) {
        registroConsumoService.excluirRegistroConsumo(id);
        return ResponseEntity.noContent().build();
    }
}
