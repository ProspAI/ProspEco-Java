package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.service.BandeiraTarifariaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bandeiras-tarifarias")
public class BandeiraTarifariaController {

    private final BandeiraTarifariaService bandeiraTarifariaService;

    @Autowired
    public BandeiraTarifariaController(BandeiraTarifariaService bandeiraTarifariaService) {
        this.bandeiraTarifariaService = bandeiraTarifariaService;
    }

    /**
     * Lista todas as bandeiras tarifárias.
     *
     * @return Lista de BandeiraTarifariaResponseDTO.
     */
    @GetMapping
    public ResponseEntity<List<BandeiraTarifariaResponseDTO>> listarTodasBandeiras() {
        List<BandeiraTarifariaResponseDTO> bandeiras = bandeiraTarifariaService.listarTodasBandeiras();
        return ResponseEntity.ok(bandeiras);
    }

    /**
     * Busca uma bandeira tarifária pelo ID.
     *
     * @param id ID da bandeira tarifária.
     * @return BandeiraTarifariaResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BandeiraTarifariaResponseDTO> buscarBandeiraPorId(@PathVariable Long id) {
        return bandeiraTarifariaService.buscarBandeiraPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria uma nova bandeira tarifária.
     *
     * @param requestDTO Dados da bandeira tarifária a ser criada.
     * @return BandeiraTarifariaResponseDTO com os dados da bandeira criada.
     */
    @PostMapping
    public ResponseEntity<BandeiraTarifariaResponseDTO> criarBandeira(@Valid @RequestBody BandeiraTarifariaRequestDTO requestDTO) {
        BandeiraTarifariaResponseDTO bandeira = bandeiraTarifariaService.criarBandeira(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bandeira);
    }

    /**
     * Atualiza os dados de uma bandeira tarifária existente.
     *
     * @param id         ID da bandeira tarifária.
     * @param requestDTO Dados de atualização da bandeira tarifária.
     * @return BandeiraTarifariaResponseDTO com os dados atualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BandeiraTarifariaResponseDTO> atualizarBandeira(@PathVariable Long id,
                                                                          @Valid @RequestBody BandeiraTarifariaRequestDTO requestDTO) {
        BandeiraTarifariaResponseDTO bandeiraAtualizada = bandeiraTarifariaService.atualizarBandeira(id, requestDTO);
        return ResponseEntity.ok(bandeiraAtualizada);
    }

    /**
     * Exclui uma bandeira tarifária.
     *
     * @param id ID da bandeira tarifária.
     * @return Resposta sem conteúdo (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirBandeira(@PathVariable Long id) {
        bandeiraTarifariaService.excluirBandeira(id);
        return ResponseEntity.noContent().build();
    }
}
