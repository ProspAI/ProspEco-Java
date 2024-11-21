package br.com.fiap.jadv.prospeco.controller.Api;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.service.ConquistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conquistas")
public class ConquistaController {

    private final ConquistaService conquistaService;

    @Autowired
    public ConquistaController(ConquistaService conquistaService) {
        this.conquistaService = conquistaService;
    }

    /**
     * Lista todas as conquistas de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @return Lista de ConquistaResponseDTO.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<ConquistaResponseDTO>> listarConquistasPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ConquistaResponseDTO> conquistas = conquistaService.listarConquistasPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(conquistas);
    }

    /**
     * Busca uma conquista pelo ID.
     *
     * @param id ID da conquista.
     * @return ConquistaResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConquistaResponseDTO> buscarConquistaPorId(@PathVariable Long id) {
        return conquistaService.buscarConquistaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria uma nova conquista.
     *
     * @param requestDTO Dados da conquista.
     * @return ConquistaResponseDTO com os dados da conquista criada.
     */
    @PostMapping
    public ResponseEntity<ConquistaResponseDTO> criarConquista(@Valid @RequestBody ConquistaRequestDTO requestDTO) {
        ConquistaResponseDTO conquista = conquistaService.criarConquista(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(conquista);
    }

    /**
     * Atualiza os dados de uma conquista existente.
     *
     * @param id         ID da conquista.
     * @param requestDTO Dados de atualização da conquista.
     * @return ConquistaResponseDTO com os dados atualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConquistaResponseDTO> atualizarConquista(@PathVariable Long id,
                                                                   @Valid @RequestBody ConquistaRequestDTO requestDTO) {
        ConquistaResponseDTO conquistaAtualizada = conquistaService.atualizarConquista(id, requestDTO);
        return ResponseEntity.ok(conquistaAtualizada);
    }

    /**
     * Exclui uma conquista.
     *
     * @param id ID da conquista.
     * @return Resposta sem conteúdo (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirConquista(@PathVariable Long id) {
        conquistaService.excluirConquista(id);
        return ResponseEntity.noContent().build();
    }
}
