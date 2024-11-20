package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.ConquistaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.ConquistaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Conquista;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.ConquistaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ConquistaServiceTest {

    @Mock
    private ConquistaRepository conquistaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ConquistaService conquistaService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        // Configura o KafkaProducerService para não fazer nada durante os testes
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any());
    }

    @Test
    public void listarConquistasPorUsuario_Sucesso() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        Conquista conquista1 = Conquista.builder()
                .id(1L)
                .titulo("Conquista 1")
                .descricao("Descrição 1")
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Conquista conquista2 = Conquista.builder()
                .id(2L)
                .titulo("Conquista 2")
                .descricao("Descrição 2")
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Page<Conquista> conquistasPage = new PageImpl<>(Arrays.asList(conquista1, conquista2), pageable, 2);

        when(conquistaRepository.findByUsuarioId(usuarioId, pageable)).thenReturn(conquistasPage);

        // Execução do método
        Page<ConquistaResponseDTO> result = conquistaService.listarConquistasPorUsuario(usuarioId, pageable);

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Conquista 1", result.getContent().get(0).getTitulo());
        assertEquals("Conquista 2", result.getContent().get(1).getTitulo());
    }

    @Test
    public void buscarConquistaPorId_Sucesso() {
        // Dados de teste
        Long conquistaId = 1L;
        Conquista conquista = Conquista.builder()
                .id(conquistaId)
                .titulo("Conquista 1")
                .descricao("Descrição 1")
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        when(conquistaRepository.findById(conquistaId)).thenReturn(Optional.of(conquista));

        // Execução do método
        Optional<ConquistaResponseDTO> result = conquistaService.buscarConquistaPorId(conquistaId);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals(conquistaId, result.get().getId());
        assertEquals("Conquista 1", result.get().getTitulo());
    }

    @Test
    public void criarConquista_Sucesso() {
        // Dados de teste
        ConquistaRequestDTO requestDTO = ConquistaRequestDTO.builder()
                .titulo("Nova Conquista")
                .descricao("Descrição da nova conquista")
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Conquista conquistaSalva = Conquista.builder()
                .id(1L)
                .titulo(requestDTO.getTitulo())
                .descricao(requestDTO.getDescricao())
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        when(conquistaRepository.save(any(Conquista.class))).thenReturn(conquistaSalva);

        // Execução do método
        ConquistaResponseDTO result = conquistaService.criarConquista(requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(requestDTO.getTitulo(), result.getTitulo());
        assertEquals(requestDTO.getDescricao(), result.getDescricao());
        assertEquals(usuario.getId(), result.getUsuarioId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("conquista-events"), any(ConquistaResponseDTO.class));
    }

    @Test
    public void criarConquista_UsuarioNaoEncontrado() {
        // Dados de teste
        ConquistaRequestDTO requestDTO = ConquistaRequestDTO.builder()
                .titulo("Nova Conquista")
                .descricao("Descrição da nova conquista")
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> conquistaService.criarConquista(requestDTO));

        // Verifica que o método save não foi chamado
        verify(conquistaRepository, never()).save(any(Conquista.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void atualizarConquista_Sucesso() {
        // Dados de teste
        Long conquistaId = 1L;
        ConquistaRequestDTO requestDTO = ConquistaRequestDTO.builder()
                .titulo("Conquista Atualizada")
                .descricao("Descrição atualizada")
                .usuarioId(usuario.getId())
                .build();

        Conquista conquistaExistente = Conquista.builder()
                .id(conquistaId)
                .titulo("Conquista Antiga")
                .descricao("Descrição antiga")
                .dataConquista(LocalDateTime.now().minusDays(1))
                .usuario(usuario)
                .build();

        when(conquistaRepository.findById(conquistaId)).thenReturn(Optional.of(conquistaExistente));
        when(conquistaRepository.save(any(Conquista.class))).thenReturn(conquistaExistente);

        // Execução do método
        ConquistaResponseDTO result = conquistaService.atualizarConquista(conquistaId, requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(conquistaId, result.getId());
        assertEquals("Conquista Atualizada", result.getTitulo());
        assertEquals("Descrição atualizada", result.getDescricao());
        assertEquals(usuario.getId(), result.getUsuarioId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("conquista-events"), any(ConquistaResponseDTO.class));
    }

    @Test
    public void atualizarConquista_NaoEncontrado() {
        // Dados de teste
        Long conquistaId = 1L;
        ConquistaRequestDTO requestDTO = ConquistaRequestDTO.builder()
                .titulo("Conquista Atualizada")
                .descricao("Descrição atualizada")
                .usuarioId(usuario.getId())
                .build();

        when(conquistaRepository.findById(conquistaId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> conquistaService.atualizarConquista(conquistaId, requestDTO));

        // Verifica que o método save não foi chamado
        verify(conquistaRepository, never()).save(any(Conquista.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void excluirConquista_Sucesso() {
        // Dados de teste
        Long conquistaId = 1L;

        Conquista conquista = Conquista.builder()
                .id(conquistaId)
                .titulo("Conquista para deletar")
                .descricao("Descrição")
                .dataConquista(LocalDateTime.now())
                .usuario(usuario)
                .build();

        when(conquistaRepository.findById(conquistaId)).thenReturn(Optional.of(conquista));

        // Execução do método
        assertDoesNotThrow(() -> conquistaService.excluirConquista(conquistaId));

        // Verificações
        verify(conquistaRepository, times(1)).delete(conquista);
        verify(kafkaProducerService, times(1)).sendMessage(eq("conquista-events"), any(ConquistaResponseDTO.class));
    }

    @Test
    public void excluirConquista_NaoEncontrado() {
        // Dados de teste
        Long conquistaId = 1L;

        when(conquistaRepository.findById(conquistaId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> conquistaService.excluirConquista(conquistaId));

        // Verifica que o método delete não foi chamado
        verify(conquistaRepository, never()).delete(any(Conquista.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }
}
