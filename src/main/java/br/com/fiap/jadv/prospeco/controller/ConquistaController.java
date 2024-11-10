package br.com.fiap.jadv.prospeco.controller;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.service.ConquistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>ConquistaController</h1>
 * Controller responsável pelos endpoints relacionados às conquistas dos usuários.
 */
@RestController
@RequestMapping("/conquistas")
@RequiredArgsConstructor
public class ConquistaController {

    private final ConquistaService conquistaService;

    /**
     * Cria uma nova conquista para um usuário específico.
     *
     * @param conquistaRequestDTO DTO contendo os dados da conquista.
     * @return DTO de resposta contendo os dados da conquista criada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConquistaResponseDTO> criarConquista(
            @Valid @RequestBody ConquistaRequestDTO conquistaRequestDTO) {
        ConquistaResponseDTO responseDTO = conquistaService.criarConquista(conquistaRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Busca todas as conquistas de um usuário específico, com suporte a paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Objeto de paginação.
     * @return Página de conquistas do usuário.
     */
    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ConquistaResponseDTO>> buscarConquistasPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        Page<ConquistaResponseDTO> conquistas = conquistaService.buscarConquistasPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(conquistas);
    }

    /**
     * Atualiza uma conquista existente.
     *
     * @param id                   ID da conquista.
     * @param conquistaRequestDTO  DTO contendo os novos dados da conquista.
     * @return DTO de resposta contendo os dados da conquista atualizada.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConquistaResponseDTO> atualizarConquista(
            @PathVariable Long id,
            @Valid @RequestBody ConquistaRequestDTO conquistaRequestDTO) {
        ConquistaResponseDTO responseDTO = conquistaService.atualizarConquista(id, conquistaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Exclui uma conquista pelo ID.
     *
     * @param id ID da conquista a ser excluída.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirConquista(@PathVariable Long id) {
        conquistaService.excluirConquista(id);
        return ResponseEntity.noContent().build();
    }
}
