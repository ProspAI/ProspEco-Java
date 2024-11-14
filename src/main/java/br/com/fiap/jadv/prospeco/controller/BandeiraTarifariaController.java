package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.service.BandeiraTarifariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/bandeiras-tarifarias")
@RequiredArgsConstructor
public class BandeiraTarifariaController {

    private final BandeiraTarifariaService bandeiraTarifariaService;

    /**
     * Endpoint para criar uma nova bandeira tarifária.
     *
     * @param requestDTO Dados da bandeira tarifária.
     * @return ResponseEntity contendo o BandeiraTarifariaResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<BandeiraTarifariaResponseDTO> criarBandeira(
            @Valid @RequestBody BandeiraTarifariaRequestDTO requestDTO) {

        BandeiraTarifariaResponseDTO response = bandeiraTarifariaService.criarBandeira(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para atualizar uma bandeira tarifária existente.
     *
     * @param id         Identificador da bandeira a ser atualizada.
     * @param requestDTO Dados de atualização da bandeira tarifária.
     * @return ResponseEntity contendo o BandeiraTarifariaResponseDTO atualizado e o status 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<BandeiraTarifariaResponseDTO> atualizarBandeira(
            @PathVariable Long id,
            @Valid @RequestBody BandeiraTarifariaRequestDTO requestDTO) {

        BandeiraTarifariaResponseDTO response = bandeiraTarifariaService.atualizarBandeira(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obter a bandeira tarifária vigente para uma data específica.
     *
     * @param dataVigencia Data para a qual buscar a bandeira tarifária.
     * @return ResponseEntity contendo o BandeiraTarifariaResponseDTO e o status 200 (OK) ou 404 (Not Found).
     */
    @GetMapping("/vigencia")
    public ResponseEntity<BandeiraTarifariaResponseDTO> obterBandeiraPorData(
            @RequestParam("data") LocalDate dataVigencia) {

        Optional<BandeiraTarifariaResponseDTO> bandeiraResponse = bandeiraTarifariaService.obterBandeiraPorData(dataVigencia);

        return bandeiraResponse
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
