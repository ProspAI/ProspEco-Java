package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.service.BandeiraTarifariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * <h1>BandeiraTarifariaController</h1>
 * Controller responsável pelos endpoints relacionados às bandeiras tarifárias.
 */
@RestController
@RequestMapping("/bandeiras-tarifarias")
@RequiredArgsConstructor
public class BandeiraTarifariaController {

    private final BandeiraTarifariaService bandeiraTarifariaService;

    /**
     * Define a bandeira tarifária para uma data específica.
     *
     * @param bandeiraTarifariaRequestDTO DTO contendo os dados da bandeira tarifária.
     * @return DTO de resposta contendo os dados da bandeira tarifária criada ou atualizada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BandeiraTarifariaResponseDTO> definirBandeiraTarifaria(
            @Valid @RequestBody BandeiraTarifariaRequestDTO bandeiraTarifariaRequestDTO) {
        BandeiraTarifariaResponseDTO responseDTO = bandeiraTarifariaService.definirBandeiraTarifaria(
                bandeiraTarifariaRequestDTO.getDataVigencia(),
                bandeiraTarifariaRequestDTO.getTipoBandeira()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Consulta a bandeira tarifária vigente para uma data específica.
     *
     * @param dataVigencia Data para a qual se deseja consultar a bandeira tarifária.
     * @return DTO de resposta contendo os dados da bandeira tarifária vigente.
     */
    @GetMapping
    public ResponseEntity<BandeiraTarifariaResponseDTO> buscarBandeiraPorData(
            @RequestParam("dataVigencia") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVigencia) {
        BandeiraTarifariaResponseDTO responseDTO = bandeiraTarifariaService.buscarBandeiraPorData(dataVigencia);
        return ResponseEntity.ok(responseDTO);
    }
}
