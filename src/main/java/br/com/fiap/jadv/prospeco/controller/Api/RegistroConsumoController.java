package br.com.fiap.jadv.prospeco.controller.Api;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RegistroConsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registro-consumo")
public class RegistroConsumoController {

    private final RegistroConsumoService registroConsumoService;

    @Autowired
    public RegistroConsumoController(RegistroConsumoService registroConsumoService) {
        this.registroConsumoService = registroConsumoService;
    }

    /**
     * Lista todos os registros de consumo de um aparelho.
     *
     * @param aparelhoId ID do aparelho.
     * @return Lista de RegistroConsumoResponseDTO.
     */
    @GetMapping("/aparelho/{aparelhoId}")
    public ResponseEntity<Page<RegistroConsumoResponseDTO>> listarRegistrosPorAparelho(
            @PathVariable Long aparelhoId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RegistroConsumoResponseDTO> registros = registroConsumoService.listarRegistrosPorAparelho(aparelhoId, pageable);
        return ResponseEntity.ok(registros);
    }

    /**
     * Busca um registro de consumo pelo ID.
     *
     * @param id ID do registro.
     * @return RegistroConsumoResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistroConsumoResponseDTO> buscarRegistroPorId(@PathVariable Long id) {
        return registroConsumoService.buscarRegistroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo registro de consumo.
     *
     * @param requestDTO Dados do registro de consumo.
     * @return RegistroConsumoResponseDTO com os dados do registro criado.
     */
    @PostMapping
    public ResponseEntity<RegistroConsumoResponseDTO> criarRegistroConsumo(@Valid @RequestBody RegistroConsumoRequestDTO requestDTO) {
        RegistroConsumoResponseDTO registro = registroConsumoService.criarRegistroConsumo(requestDTO);
        return ResponseEntity.status(201).body(registro);
    }

    /**
     * Atualiza um registro de consumo existente.
     *
     * @param id         ID do registro.
     * @param requestDTO Dados atualizados do registro.
     * @return RegistroConsumoResponseDTO com os dados atualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegistroConsumoResponseDTO> atualizarRegistroConsumo(@PathVariable Long id,
                                                                               @Valid @RequestBody RegistroConsumoRequestDTO requestDTO) {
        RegistroConsumoResponseDTO registroAtualizado = registroConsumoService.atualizarRegistroConsumo(id, requestDTO);
        return ResponseEntity.ok(registroAtualizado);
    }

    /**
     * Exclui um registro de consumo.
     *
     * @param id ID do registro.
     * @return Resposta sem conteúdo (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirRegistroConsumo(@PathVariable Long id) {
        registroConsumoService.excluirRegistroConsumo(id);
        return ResponseEntity.noContent().build();
    }
}
