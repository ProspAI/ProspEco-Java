package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AparelhoServiceTest {

    @Mock
    private AparelhoRepository aparelhoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private AparelhoService aparelhoService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        // Mock KafkaProducerService
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any());
    }

    @Test
    public void listarAparelhosPorUsuario_Sucesso() {
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Aparelho aparelho = new Aparelho();
        aparelho.setId(1L);
        aparelho.setUsuario(usuario);

        when(aparelhoRepository.findByUsuarioId(usuarioId, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(aparelho)));

        Page<AparelhoResponseDTO> result = aparelhoService.listarAparelhosPorUsuario(usuarioId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void buscarAparelhoPorId_Sucesso() {
        Long aparelhoId = 1L;
        Aparelho aparelho = new Aparelho();
        aparelho.setId(aparelhoId);
        aparelho.setUsuario(usuario);

        when(aparelhoRepository.findById(aparelhoId)).thenReturn(Optional.of(aparelho));

        Optional<AparelhoResponseDTO> result = aparelhoService.buscarAparelhoPorId(aparelhoId);

        assertTrue(result.isPresent());
        assertEquals(aparelhoId, result.get().getId());
    }

    @Test
    public void criarAparelho_Sucesso() {
        Long usuarioId = usuario.getId();
        AparelhoRequestDTO requestDTO = new AparelhoRequestDTO();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Aparelho aparelho = new Aparelho();
        aparelho.setId(1L);
        aparelho.setUsuario(usuario);

        when(aparelhoRepository.save(any(Aparelho.class))).thenReturn(aparelho);

        AparelhoResponseDTO result = aparelhoService.criarAparelho(requestDTO, usuarioId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(kafkaProducerService, times(1)).sendMessage(anyString(), any());
    }

    @Test
    public void atualizarAparelho_Sucesso() {
        Long aparelhoId = 1L;
        AparelhoRequestDTO requestDTO = new AparelhoRequestDTO();
        Aparelho aparelho = new Aparelho();
        aparelho.setId(aparelhoId);
        aparelho.setUsuario(usuario);

        when(aparelhoRepository.findById(aparelhoId)).thenReturn(Optional.of(aparelho));
        when(aparelhoRepository.save(any(Aparelho.class))).thenReturn(aparelho);

        AparelhoResponseDTO result = aparelhoService.atualizarAparelho(aparelhoId, requestDTO);

        assertNotNull(result);
        assertEquals(aparelhoId, result.getId());
        verify(kafkaProducerService, times(1)).sendMessage(anyString(), any());
    }

    @Test
    public void excluirAparelho_Sucesso() {
        Long aparelhoId = 1L;
        Aparelho aparelho = new Aparelho();
        aparelho.setId(aparelhoId);
        aparelho.setUsuario(usuario);

        when(aparelhoRepository.findById(aparelhoId)).thenReturn(Optional.of(aparelho));

        assertDoesNotThrow(() -> aparelhoService.excluirAparelho(aparelhoId));
        verify(aparelhoRepository, times(1)).delete(aparelho);
        verify(kafkaProducerService, times(1)).sendMessage(anyString(), any());
    }

    @Test
    public void criarAparelho_UsuarioNaoEncontrado() {
        Long usuarioId = usuario.getId();
        AparelhoRequestDTO requestDTO = new AparelhoRequestDTO();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> aparelhoService.criarAparelho(requestDTO, usuarioId));
    }
}
