package br.com.fiap.jadv.prospeco.controller.Api;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.service.AparelhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aparelhos")
public class AparelhoController {

    private final AparelhoService aparelhoService;

    @Autowired
    public AparelhoController(AparelhoService aparelhoService) {
        this.aparelhoService = aparelhoService;
    }

    /**
     * Lista todos os aparelhos de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de AparelhoResponseDTO.
     */
    @GetMapping("/{usuarioId}/aparelhos")
    public ResponseEntity<Page<AparelhoResponseDTO>> listarAparelhos(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AparelhoResponseDTO> aparelhos = aparelhoService.listarAparelhosPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(aparelhos);
    }

    /**
     * Busca um aparelho por ID.
     *
     * @param id ID do aparelho.
     * @return AparelhoResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> buscarAparelhoPorId(@PathVariable Long id) {
        return aparelhoService.buscarAparelhoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo aparelho.
     *
     * @param usuarioId  ID do usuário proprietário.
     * @param requestDTO Dados do aparelho a ser criado.
     * @return AparelhoResponseDTO com os dados do aparelho criado.
     */
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<AparelhoResponseDTO> criarAparelho(@PathVariable Long usuarioId,
                                                             @Valid @RequestBody AparelhoRequestDTO requestDTO) {
        AparelhoResponseDTO aparelho = aparelhoService.criarAparelho(requestDTO, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(aparelho);
    }

    /**
     * Atualiza um aparelho existente.
     *
     * @param id         ID do aparelho.
     * @param requestDTO Dados do aparelho a serem atualizados.
     * @return AparelhoResponseDTO com os dados atualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> atualizarAparelho(@PathVariable Long id,
                                                                 @Valid @RequestBody AparelhoRequestDTO requestDTO) {
        AparelhoResponseDTO aparelhoAtualizado = aparelhoService.atualizarAparelho(id, requestDTO);
        return ResponseEntity.ok(aparelhoAtualizado);
    }

    /**
     * Exclui um aparelho.
     *
     * @param id ID do aparelho.
     * @return Resposta sem conteúdo (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAparelho(@PathVariable Long id) {
        aparelhoService.excluirAparelho(id);
        return ResponseEntity.noContent().build();
    }
}
