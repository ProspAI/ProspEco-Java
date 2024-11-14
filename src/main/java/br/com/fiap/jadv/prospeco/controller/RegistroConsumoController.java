package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RegistroConsumoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registros-consumo")
@RequiredArgsConstructor
public class RegistroConsumoController {

    private final RegistroConsumoService registroConsumoService;

    /**
     * Cria um novo registro de consumo para um aparelho específico.
     *
     * @param requestDTO Dados do registro de consumo.
     * @return ResponseEntity contendo o RegistroConsumoResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<RegistroConsumoResponseDTO> criarRegistroConsumo(
            @Valid @RequestBody RegistroConsumoRequestDTO requestDTO) {

        RegistroConsumoResponseDTO responseDTO = registroConsumoService.criarRegistroConsumo(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Lista registros de consumo de um aparelho específico com paginação.
     *
     * @param aparelhoId ID do aparelho ao qual os registros pertencem.
     * @param pageable   Configuração de paginação.
     * @return ResponseEntity contendo a página de RegistroConsumoResponseDTO e o status 200 (OK).
     */
    @GetMapping("/aparelhos/{aparelhoId}")
    public ResponseEntity<Page<RegistroConsumoResponseDTO>> listarRegistrosPorAparelho(
            @PathVariable Long aparelhoId, Pageable pageable) {

        Page<RegistroConsumoResponseDTO> registros = registroConsumoService.listarRegistrosPorAparelho(aparelhoId, pageable);
        return ResponseEntity.ok(registros);
    }
}
