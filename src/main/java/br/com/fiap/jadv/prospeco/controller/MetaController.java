package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.service.MetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
public class MetaController {

    private final MetaService metaService;

    @Autowired
    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    /**
     * Lista todas as metas de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de MetaResponseDTO.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<MetaResponseDTO>> listarMetasPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<MetaResponseDTO> metas = metaService.listarMetasPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(metas);
    }

    /**
     * Busca uma meta pelo ID.
     *
     * @param id ID da meta.
     * @return MetaResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> buscarMetaPorId(@PathVariable Long id) {
        return metaService.buscarMetaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria uma nova meta.
     *
     * @param requestDTO Dados da meta a ser criada.
     * @return MetaResponseDTO com os dados da meta criada.
     */
    @PostMapping
    public ResponseEntity<MetaResponseDTO> criarMeta(@Valid @RequestBody MetaRequestDTO requestDTO) {
        MetaResponseDTO meta = metaService.criarMeta(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(meta);
    }

    /**
     * Atualiza os dados de uma meta existente.
     *
     * @param id         ID da meta.
     * @param requestDTO Dados de atualização da meta.
     * @return MetaResponseDTO com os dados atualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> atualizarMeta(@PathVariable Long id,
                                                         @Valid @RequestBody MetaRequestDTO requestDTO) {
        MetaResponseDTO metaAtualizada = metaService.atualizarMeta(id, requestDTO);
        return ResponseEntity.ok(metaAtualizada);
    }

    /**
     * Exclui uma meta existente.
     *
     * @param id ID da meta.
     * @return Resposta sem conteúdo (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMeta(@PathVariable Long id) {
        metaService.excluirMeta(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca uma meta como atingida.
     *
     * @param id ID da meta.
     * @return Resposta com os dados da meta marcada como atingida.
     */
    @PatchMapping("/{id}/atingida")
    public ResponseEntity<MetaResponseDTO> marcarMetaComoAtingida(@PathVariable Long id) {
        metaService.marcarMetaComoAtingida(id);
        return ResponseEntity.ok().build();
    }
}
