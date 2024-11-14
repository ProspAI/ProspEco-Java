package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.service.ConquistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conquistas")
@RequiredArgsConstructor
public class ConquistaController {

    private final ConquistaService conquistaService;

    /**
     * Endpoint para criar uma nova conquista para um usuário específico.
     *
     * @param requestDTO Dados da nova conquista.
     * @return ResponseEntity contendo o ConquistaResponseDTO e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ConquistaResponseDTO> criarConquista(
            @Valid @RequestBody ConquistaRequestDTO requestDTO) {

        ConquistaResponseDTO response = conquistaService.criarConquista(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para listar conquistas de um usuário específico com paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de ConquistaResponseDTO.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<ConquistaResponseDTO>> listarConquistasPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {

        Page<ConquistaResponseDTO> conquistas = conquistaService.listarConquistasPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(conquistas);
    }
}
