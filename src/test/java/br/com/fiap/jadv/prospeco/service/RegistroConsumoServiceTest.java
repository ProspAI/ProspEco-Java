package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RegistroConsumoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RegistroConsumoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.RegistroConsumoRepository;
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
public class RegistroConsumoServiceTest {

    @Mock
    private RegistroConsumoRepository registroConsumoRepository;

    @Mock
    private AparelhoRepository aparelhoRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private RegistroConsumoService registroConsumoService;

    private Aparelho aparelho;

    @BeforeEach
    public void setUp() {
        aparelho = new Aparelho();
        aparelho.setId(1L);

        // Configura o KafkaProducerService para não fazer nada durante os testes
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any());
    }

    @Test
    public void listarRegistrosPorAparelho_Sucesso() {
        // Dados de teste
        Long aparelhoId = aparelho.getId();
        Pageable pageable = PageRequest.of(0, 10);

        RegistroConsumo registro1 = RegistroConsumo.builder()
                .id(1L)
                .dataHora(LocalDateTime.now())
                .consumo(10.5)
                .aparelho(aparelho)
                .build();

        RegistroConsumo registro2 = RegistroConsumo.builder()
                .id(2L)
                .dataHora(LocalDateTime.now())
                .consumo(15.0)
                .aparelho(aparelho)
                .build();

        Page<RegistroConsumo> registrosPage = new PageImpl<>(Arrays.asList(registro1, registro2), pageable, 2);

        when(aparelhoRepository.findById(aparelhoId)).thenReturn(Optional.of(aparelho));
        when(registroConsumoRepository.findByAparelho(aparelho, pageable)).thenReturn(registrosPage);

        // Execução do método
        Page<RegistroConsumoResponseDTO> result = registroConsumoService.listarRegistrosPorAparelho(aparelhoId, pageable);

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(10.5, result.getContent().get(0).getConsumo());
        assertEquals(15.0, result.getContent().get(1).getConsumo());
    }

    @Test
    public void listarRegistrosPorAparelho_AparelhoNaoEncontrado() {
        // Dados de teste
        Long aparelhoId = aparelho.getId();
        Pageable pageable = PageRequest.of(0, 10);

        when(aparelhoRepository.findById(aparelhoId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> registroConsumoService.listarRegistrosPorAparelho(aparelhoId, pageable));

        // Verifica que o repositório de registros não foi chamado
        verify(registroConsumoRepository, never()).findByAparelho(any(Aparelho.class), any(Pageable.class));
    }

    @Test
    public void buscarRegistroPorId_Sucesso() {
        // Dados de teste
        Long registroId = 1L;
        RegistroConsumo registro = RegistroConsumo.builder()
                .id(registroId)
                .dataHora(LocalDateTime.now())
                .consumo(10.5)
                .aparelho(aparelho)
                .build();

        when(registroConsumoRepository.findById(registroId)).thenReturn(Optional.of(registro));

        // Execução do método
        Optional<RegistroConsumoResponseDTO> result = registroConsumoService.buscarRegistroPorId(registroId);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals(registroId, result.get().getId());
        assertEquals(10.5, result.get().getConsumo());
    }

    @Test
    public void buscarRegistroPorId_NaoEncontrado() {
        // Dados de teste
        Long registroId = 1L;

        when(registroConsumoRepository.findById(registroId)).thenReturn(Optional.empty());

        // Execução do método
        Optional<RegistroConsumoResponseDTO> result = registroConsumoService.buscarRegistroPorId(registroId);

        // Verificações
        assertFalse(result.isPresent());
    }

    @Test
    public void criarRegistroConsumo_Sucesso() {
        // Dados de teste
        RegistroConsumoRequestDTO requestDTO = RegistroConsumoRequestDTO.builder()
                .dataHora(LocalDateTime.now())
                .consumo(12.0)
                .aparelhoId(aparelho.getId())
                .build();

        when(aparelhoRepository.findById(aparelho.getId())).thenReturn(Optional.of(aparelho));

        RegistroConsumo registroSalvo = RegistroConsumo.builder()
                .id(1L)
                .dataHora(requestDTO.getDataHora())
                .consumo(requestDTO.getConsumo())
                .aparelho(aparelho)
                .build();

        when(registroConsumoRepository.save(any(RegistroConsumo.class))).thenReturn(registroSalvo);

        // Execução do método
        RegistroConsumoResponseDTO result = registroConsumoService.criarRegistroConsumo(requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(requestDTO.getConsumo(), result.getConsumo());
        assertEquals(aparelho.getId(), result.getAparelhoId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("registro-consumo-events"), any(RegistroConsumoResponseDTO.class));
    }

    @Test
    public void criarRegistroConsumo_AparelhoNaoEncontrado() {
        // Dados de teste
        RegistroConsumoRequestDTO requestDTO = RegistroConsumoRequestDTO.builder()
                .dataHora(LocalDateTime.now())
                .consumo(12.0)
                .aparelhoId(aparelho.getId())
                .build();

        when(aparelhoRepository.findById(aparelho.getId())).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> registroConsumoService.criarRegistroConsumo(requestDTO));

        // Verifica que o método save não foi chamado
        verify(registroConsumoRepository, never()).save(any(RegistroConsumo.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void atualizarRegistroConsumo_Sucesso() {
        // Dados de teste
        Long registroId = 1L;
        RegistroConsumoRequestDTO requestDTO = RegistroConsumoRequestDTO.builder()
                .dataHora(LocalDateTime.now())
                .consumo(14.0)
                .aparelhoId(aparelho.getId())
                .build();

        RegistroConsumo registroExistente = RegistroConsumo.builder()
                .id(registroId)
                .dataHora(LocalDateTime.now().minusDays(1))
                .consumo(12.0)
                .aparelho(aparelho)
                .build();

        when(registroConsumoRepository.findById(registroId)).thenReturn(Optional.of(registroExistente));
        when(registroConsumoRepository.save(any(RegistroConsumo.class))).thenReturn(registroExistente);

        // Execução do método
        RegistroConsumoResponseDTO result = registroConsumoService.atualizarRegistroConsumo(registroId, requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(registroId, result.getId());
        assertEquals(14.0, result.getConsumo());
        assertEquals(aparelho.getId(), result.getAparelhoId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("registro-consumo-events"), any(RegistroConsumoResponseDTO.class));
    }

    @Test
    public void atualizarRegistroConsumo_NaoEncontrado() {
        // Dados de teste
        Long registroId = 1L;
        RegistroConsumoRequestDTO requestDTO = RegistroConsumoRequestDTO.builder()
                .dataHora(LocalDateTime.now())
                .consumo(14.0)
                .aparelhoId(aparelho.getId())
                .build();

        when(registroConsumoRepository.findById(registroId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> registroConsumoService.atualizarRegistroConsumo(registroId, requestDTO));

        // Verifica que o método save não foi chamado
        verify(registroConsumoRepository, never()).save(any(RegistroConsumo.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void excluirRegistroConsumo_Sucesso() {
        // Dados de teste
        Long registroId = 1L;

        RegistroConsumo registro = RegistroConsumo.builder()
                .id(registroId)
                .dataHora(LocalDateTime.now())
                .consumo(12.0)
                .aparelho(aparelho)
                .build();

        when(registroConsumoRepository.findById(registroId)).thenReturn(Optional.of(registro));

        // Execução do método
        assertDoesNotThrow(() -> registroConsumoService.excluirRegistroConsumo(registroId));

        // Verificações
        verify(registroConsumoRepository, times(1)).delete(registro);
        verify(kafkaProducerService, times(1)).sendMessage(eq("registro-consumo-events"), any(RegistroConsumoResponseDTO.class));
    }

    @Test
    public void excluirRegistroConsumo_NaoEncontrado() {
        // Dados de teste
        Long registroId = 1L;

        when(registroConsumoRepository.findById(registroId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> registroConsumoService.excluirRegistroConsumo(registroId));

        // Verifica que o método delete não foi chamado
        verify(registroConsumoRepository, never()).delete(any(RegistroConsumo.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }
}
