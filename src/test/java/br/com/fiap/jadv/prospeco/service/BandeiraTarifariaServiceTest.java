package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.BandeiraTarifariaRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.BandeiraTarifariaResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.BandeiraTarifaria;
import br.com.fiap.jadv.prospeco.model.TipoBandeira;
import br.com.fiap.jadv.prospeco.repository.BandeiraTarifariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BandeiraTarifariaServiceTest {

    @Mock
    private BandeiraTarifariaRepository bandeiraTarifariaRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private BandeiraTarifariaService bandeiraTarifariaService;

    @BeforeEach
    public void setUp() {
        // Configurando o comportamento do kafkaProducerService para não fazer nada
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any());
    }

    @Test
    public void listarTodasBandeiras_Sucesso() {
        // Dados de teste
        BandeiraTarifaria bandeira1 = new BandeiraTarifaria();
        bandeira1.setId(1L);
        bandeira1.setTipoBandeira(TipoBandeira.VERDE);
        bandeira1.setDataVigencia(LocalDate.now());

        BandeiraTarifaria bandeira2 = new BandeiraTarifaria();
        bandeira2.setId(2L);
        bandeira2.setTipoBandeira(TipoBandeira.AMARELA);
        bandeira2.setDataVigencia(LocalDate.now().plusDays(1));

        when(bandeiraTarifariaRepository.findAll()).thenReturn(Arrays.asList(bandeira1, bandeira2));

        // Chamada do método a ser testado
        List<BandeiraTarifariaResponseDTO> result = bandeiraTarifariaService.listarTodasBandeiras();

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.size());

        BandeiraTarifariaResponseDTO response1 = result.get(0);
        BandeiraTarifariaResponseDTO response2 = result.get(1);

        assertEquals(1L, response1.getId());
        assertEquals(TipoBandeira.VERDE, response1.getTipoBandeira());
        assertEquals(bandeira1.getDataVigencia(), response1.getDataVigencia());

        assertEquals(2L, response2.getId());
        assertEquals(TipoBandeira.AMARELA, response2.getTipoBandeira());
        assertEquals(bandeira2.getDataVigencia(), response2.getDataVigencia());
    }

    @Test
    public void buscarBandeiraPorId_Sucesso() {
        // Dados de teste
        Long bandeiraId = 1L;
        BandeiraTarifaria bandeira = new BandeiraTarifaria();
        bandeira.setId(bandeiraId);
        bandeira.setTipoBandeira(TipoBandeira.VERMELHA_1);
        bandeira.setDataVigencia(LocalDate.now());

        when(bandeiraTarifariaRepository.findById(bandeiraId)).thenReturn(Optional.of(bandeira));

        // Chamada do método a ser testado
        Optional<BandeiraTarifariaResponseDTO> result = bandeiraTarifariaService.buscarBandeiraPorId(bandeiraId);

        // Verificações
        assertTrue(result.isPresent());
        BandeiraTarifariaResponseDTO response = result.get();

        assertEquals(bandeiraId, response.getId());
        assertEquals(TipoBandeira.VERMELHA_1, response.getTipoBandeira());
        assertEquals(bandeira.getDataVigencia(), response.getDataVigencia());
    }

    @Test
    public void buscarBandeiraPorId_NaoEncontrado() {
        // Dados de teste
        Long bandeiraId = 1L;
        when(bandeiraTarifariaRepository.findById(bandeiraId)).thenReturn(Optional.empty());

        // Chamada do método a ser testado
        Optional<BandeiraTarifariaResponseDTO> result = bandeiraTarifariaService.buscarBandeiraPorId(bandeiraId);

        // Verificações
        assertFalse(result.isPresent());
    }

    @Test
    public void criarBandeira_Sucesso() {
        // Dados de teste
        BandeiraTarifariaRequestDTO requestDTO = BandeiraTarifariaRequestDTO.builder()
                .tipoBandeira(TipoBandeira.AMARELA)
                .dataVigencia(LocalDate.now().plusDays(5))
                .build();

        BandeiraTarifaria bandeiraSalva = new BandeiraTarifaria();
        bandeiraSalva.setId(1L);
        bandeiraSalva.setTipoBandeira(requestDTO.getTipoBandeira());
        bandeiraSalva.setDataVigencia(requestDTO.getDataVigencia());

        when(bandeiraTarifariaRepository.save(any(BandeiraTarifaria.class))).thenReturn(bandeiraSalva);

        // Chamada do método a ser testado
        BandeiraTarifariaResponseDTO responseDTO = bandeiraTarifariaService.criarBandeira(requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(TipoBandeira.AMARELA, responseDTO.getTipoBandeira());
        assertEquals(requestDTO.getDataVigencia(), responseDTO.getDataVigencia());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("bandeira-tarifaria-events"), any(BandeiraTarifariaResponseDTO.class));
    }

    @Test
    public void atualizarBandeira_Sucesso() {
        // Dados de teste
        Long bandeiraId = 1L;
        BandeiraTarifariaRequestDTO requestDTO = BandeiraTarifariaRequestDTO.builder()
                .tipoBandeira(TipoBandeira.VERMELHA_2)
                .dataVigencia(LocalDate.now().plusDays(10))
                .build();

        BandeiraTarifaria bandeiraExistente = new BandeiraTarifaria();
        bandeiraExistente.setId(bandeiraId);
        bandeiraExistente.setTipoBandeira(TipoBandeira.VERMELHA_1);
        bandeiraExistente.setDataVigencia(LocalDate.now());

        when(bandeiraTarifariaRepository.findById(bandeiraId)).thenReturn(Optional.of(bandeiraExistente));
        when(bandeiraTarifariaRepository.save(any(BandeiraTarifaria.class))).thenReturn(bandeiraExistente);

        // Chamada do método a ser testado
        BandeiraTarifariaResponseDTO responseDTO = bandeiraTarifariaService.atualizarBandeira(bandeiraId, requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals(bandeiraId, responseDTO.getId());
        assertEquals(TipoBandeira.VERMELHA_2, responseDTO.getTipoBandeira());
        assertEquals(requestDTO.getDataVigencia(), responseDTO.getDataVigencia());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("bandeira-tarifaria-events"), any(BandeiraTarifariaResponseDTO.class));
    }

    @Test
    public void atualizarBandeira_NaoEncontrado() {
        // Dados de teste
        Long bandeiraId = 1L;
        BandeiraTarifariaRequestDTO requestDTO = new BandeiraTarifariaRequestDTO();
        when(bandeiraTarifariaRepository.findById(bandeiraId)).thenReturn(Optional.empty());

        // Chamada e verificação
        assertThrows(ResourceNotFoundException.class, () -> bandeiraTarifariaService.atualizarBandeira(bandeiraId, requestDTO));

        // Verifica se o método save não foi chamado
        verify(bandeiraTarifariaRepository, never()).save(any(BandeiraTarifaria.class));

        // Verifica se o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void excluirBandeira_Sucesso() {
        // Dados de teste
        Long bandeiraId = 1L;
        BandeiraTarifaria bandeira = new BandeiraTarifaria();
        bandeira.setId(bandeiraId);
        bandeira.setTipoBandeira(TipoBandeira.VERDE);
        bandeira.setDataVigencia(LocalDate.now());

        when(bandeiraTarifariaRepository.findById(bandeiraId)).thenReturn(Optional.of(bandeira));

        // Chamada do método a ser testado
        assertDoesNotThrow(() -> bandeiraTarifariaService.excluirBandeira(bandeiraId));

        // Verificações
        verify(bandeiraTarifariaRepository, times(1)).delete(bandeira);

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("bandeira-tarifaria-events"), any(BandeiraTarifariaResponseDTO.class));
    }

    @Test
    public void excluirBandeira_NaoEncontrado() {
        // Dados de teste
        Long bandeiraId = 1L;
        when(bandeiraTarifariaRepository.findById(bandeiraId)).thenReturn(Optional.empty());

        // Chamada e verificação
        assertThrows(ResourceNotFoundException.class, () -> bandeiraTarifariaService.excluirBandeira(bandeiraId));

        // Verifica se o método delete não foi chamado
        verify(bandeiraTarifariaRepository, never()).delete(any(BandeiraTarifaria.class));

        // Verifica se o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }
}
