package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.MetaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.MetaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.MetaRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MetaServiceTest {

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private MetaService metaService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        // Configura o KafkaProducerService para não fazer nada durante os testes
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any());
    }

    @Test
    public void listarMetasPorUsuario_Sucesso() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        Meta meta1 = Meta.builder()
                .id(1L)
                .consumoAlvo(100.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .atingida(false)
                .usuario(usuario)
                .build();

        Meta meta2 = Meta.builder()
                .id(2L)
                .consumoAlvo(200.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(60))
                .atingida(false)
                .usuario(usuario)
                .build();

        Page<Meta> metasPage = new PageImpl<>(Arrays.asList(meta1, meta2), pageable, 2);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(metaRepository.findByUsuario(usuario, pageable)).thenReturn(metasPage);

        // Execução do método
        Page<MetaResponseDTO> result = metaService.listarMetasPorUsuario(usuarioId, pageable);

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(100.0, result.getContent().get(0).getConsumoAlvo());
        assertEquals(200.0, result.getContent().get(1).getConsumoAlvo());
    }

    @Test
    public void listarMetasPorUsuario_UsuarioNaoEncontrado() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> metaService.listarMetasPorUsuario(usuarioId, pageable));

        // Verifica que o repositório de metas não foi chamado
        verify(metaRepository, never()).findByUsuario(any(Usuario.class), any(Pageable.class));
    }

    @Test
    public void buscarMetaPorId_Sucesso() {
        // Dados de teste
        Long metaId = 1L;
        Meta meta = Meta.builder()
                .id(metaId)
                .consumoAlvo(150.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(45))
                .atingida(false)
                .usuario(usuario)
                .build();

        when(metaRepository.findById(metaId)).thenReturn(Optional.of(meta));

        // Execução do método
        Optional<MetaResponseDTO> result = metaService.buscarMetaPorId(metaId);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals(metaId, result.get().getId());
        assertEquals(150.0, result.get().getConsumoAlvo());
    }

    @Test
    public void buscarMetaPorId_NaoEncontrada() {
        // Dados de teste
        Long metaId = 1L;
        when(metaRepository.findById(metaId)).thenReturn(Optional.empty());

        // Execução do método
        Optional<MetaResponseDTO> result = metaService.buscarMetaPorId(metaId);

        // Verificações
        assertFalse(result.isPresent());
    }

    @Test
    public void criarMeta_Sucesso() {
        // Dados de teste
        MetaRequestDTO requestDTO = MetaRequestDTO.builder()
                .consumoAlvo(120.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Meta metaSalva = Meta.builder()
                .id(1L)
                .consumoAlvo(requestDTO.getConsumoAlvo())
                .dataInicio(requestDTO.getDataInicio())
                .dataFim(requestDTO.getDataFim())
                .atingida(false)
                .usuario(usuario)
                .build();

        when(metaRepository.save(any(Meta.class))).thenReturn(metaSalva);

        // Execução do método
        MetaResponseDTO result = metaService.criarMeta(requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(requestDTO.getConsumoAlvo(), result.getConsumoAlvo());
        assertEquals(usuario.getId(), result.getUsuarioId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("meta-events"), any(MetaResponseDTO.class));
    }

    @Test
    public void criarMeta_UsuarioNaoEncontrado() {
        // Dados de teste
        MetaRequestDTO requestDTO = MetaRequestDTO.builder()
                .consumoAlvo(120.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> metaService.criarMeta(requestDTO));

        // Verifica que o método save não foi chamado
        verify(metaRepository, never()).save(any(Meta.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void atualizarMeta_Sucesso() {
        // Dados de teste
        Long metaId = 1L;
        MetaRequestDTO requestDTO = MetaRequestDTO.builder()
                .consumoAlvo(130.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(35))
                .usuarioId(usuario.getId())
                .build();

        Meta metaExistente = Meta.builder()
                .id(metaId)
                .consumoAlvo(120.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .atingida(false)
                .usuario(usuario)
                .build();

        when(metaRepository.findById(metaId)).thenReturn(Optional.of(metaExistente));
        when(metaRepository.save(any(Meta.class))).thenReturn(metaExistente);

        // Execução do método
        MetaResponseDTO result = metaService.atualizarMeta(metaId, requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(metaId, result.getId());
        assertEquals(130.0, result.getConsumoAlvo());
        assertEquals(usuario.getId(), result.getUsuarioId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("meta-events"), any(MetaResponseDTO.class));
    }

    @Test
    public void atualizarMeta_NaoEncontrada() {
        // Dados de teste
        Long metaId = 1L;
        MetaRequestDTO requestDTO = MetaRequestDTO.builder()
                .consumoAlvo(130.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(35))
                .usuarioId(usuario.getId())
                .build();

        when(metaRepository.findById(metaId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> metaService.atualizarMeta(metaId, requestDTO));

        // Verifica que o método save não foi chamado
        verify(metaRepository, never()).save(any(Meta.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void excluirMeta_Sucesso() {
        // Dados de teste
        Long metaId = 1L;

        Meta meta = Meta.builder()
                .id(metaId)
                .consumoAlvo(120.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .atingida(false)
                .usuario(usuario)
                .build();

        when(metaRepository.findById(metaId)).thenReturn(Optional.of(meta));

        // Execução do método
        assertDoesNotThrow(() -> metaService.excluirMeta(metaId));

        // Verificações
        verify(metaRepository, times(1)).delete(meta);
        verify(kafkaProducerService, times(1)).sendMessage(eq("meta-events"), any(MetaResponseDTO.class));
    }

    @Test
    public void excluirMeta_NaoEncontrada() {
        // Dados de teste
        Long metaId = 1L;

        when(metaRepository.findById(metaId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> metaService.excluirMeta(metaId));

        // Verifica que o método delete não foi chamado
        verify(metaRepository, never()).delete(any(Meta.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void marcarMetaComoAtingida_Sucesso() {
        // Dados de teste
        Long metaId = 1L;

        Meta meta = Meta.builder()
                .id(metaId)
                .consumoAlvo(100.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .atingida(false)
                .usuario(usuario)
                .build();

        Meta metaAtingida = Meta.builder()
                .id(metaId)
                .consumoAlvo(100.0)
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(30))
                .atingida(true)
                .usuario(usuario)
                .build();

        when(metaRepository.findById(metaId)).thenReturn(Optional.of(meta));
        when(metaRepository.save(any(Meta.class))).thenReturn(metaAtingida);

        // Execução do método
        assertDoesNotThrow(() -> metaService.marcarMetaComoAtingida(metaId));

        // Verificações
        verify(metaRepository, times(1)).save(any(Meta.class));
        verify(kafkaProducerService, times(1)).sendMessage(eq("meta-events"), any(MetaResponseDTO.class));
    }

    @Test
    public void marcarMetaComoAtingida_NaoEncontrada() {
        // Dados de teste
        Long metaId = 1L;

        when(metaRepository.findById(metaId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> metaService.marcarMetaComoAtingida(metaId));

        // Verifica que o método save não foi chamado
        verify(metaRepository, never()).save(any(Meta.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }
}
