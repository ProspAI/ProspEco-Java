package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.NotificacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.NotificacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Notificacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.NotificacaoRepository;
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
public class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        // Configura o KafkaProducerService para não fazer nada durante os testes
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any());
    }

    @Test
    public void listarNotificacoesPorUsuario_Sucesso() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        Notificacao notificacao1 = Notificacao.builder()
                .id(1L)
                .mensagem("Notificação 1")
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        Notificacao notificacao2 = Notificacao.builder()
                .id(2L)
                .mensagem("Notificação 2")
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        Page<Notificacao> notificacoesPage = new PageImpl<>(Arrays.asList(notificacao1, notificacao2), pageable, 2);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)).thenReturn(notificacoesPage);

        // Execução do método
        Page<NotificacaoResponseDTO> result = notificacaoService.listarNotificacoesPorUsuario(usuarioId, pageable);

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Notificação 1", result.getContent().get(0).getMensagem());
        assertEquals("Notificação 2", result.getContent().get(1).getMensagem());
    }

    @Test
    public void listarNotificacoesPorUsuario_UsuarioNaoEncontrado() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> notificacaoService.listarNotificacoesPorUsuario(usuarioId, pageable));

        // Verifica que o repositório de notificações não foi chamado
        verify(notificacaoRepository, never()).findByUsuarioOrderByDataHoraDesc(any(Usuario.class), any(Pageable.class));
    }

    @Test
    public void buscarNotificacaoPorId_Sucesso() {
        // Dados de teste
        Long notificacaoId = 1L;
        Notificacao notificacao = Notificacao.builder()
                .id(notificacaoId)
                .mensagem("Notificação de teste")
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        when(notificacaoRepository.findById(notificacaoId)).thenReturn(Optional.of(notificacao));

        // Execução do método
        Optional<NotificacaoResponseDTO> result = notificacaoService.buscarNotificacaoPorId(notificacaoId);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals(notificacaoId, result.get().getId());
        assertEquals("Notificação de teste", result.get().getMensagem());
    }

    @Test
    public void buscarNotificacaoPorId_NaoEncontrada() {
        // Dados de teste
        Long notificacaoId = 1L;
        when(notificacaoRepository.findById(notificacaoId)).thenReturn(Optional.empty());

        // Execução do método
        Optional<NotificacaoResponseDTO> result = notificacaoService.buscarNotificacaoPorId(notificacaoId);

        // Verificações
        assertFalse(result.isPresent());
    }

    @Test
    public void criarNotificacao_Sucesso() {
        // Dados de teste
        NotificacaoRequestDTO requestDTO = NotificacaoRequestDTO.builder()
                .mensagem("Nova notificação")
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Notificacao notificacaoSalva = Notificacao.builder()
                .id(1L)
                .mensagem(requestDTO.getMensagem())
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        when(notificacaoRepository.save(any(Notificacao.class))).thenReturn(notificacaoSalva);

        // Execução do método
        NotificacaoResponseDTO result = notificacaoService.criarNotificacao(requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(requestDTO.getMensagem(), result.getMensagem());
        assertEquals(usuario.getId(), result.getUsuarioId());

        // Verifica se o evento Kafka foi enviado
        verify(kafkaProducerService, times(1)).sendMessage(eq("notificacao-events"), any(NotificacaoResponseDTO.class));
    }

    @Test
    public void criarNotificacao_UsuarioNaoEncontrado() {
        // Dados de teste
        NotificacaoRequestDTO requestDTO = NotificacaoRequestDTO.builder()
                .mensagem("Nova notificação")
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> notificacaoService.criarNotificacao(requestDTO));

        // Verifica que o método save não foi chamado
        verify(notificacaoRepository, never()).save(any(Notificacao.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void marcarNotificacaoComoLida_Sucesso() {
        // Dados de teste
        Long notificacaoId = 1L;

        Notificacao notificacao = Notificacao.builder()
                .id(notificacaoId)
                .mensagem("Notificação para marcar como lida")
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        Notificacao notificacaoLida = Notificacao.builder()
                .id(notificacaoId)
                .mensagem("Notificação para marcar como lida")
                .dataHora(LocalDateTime.now())
                .lida(true)
                .usuario(usuario)
                .build();

        when(notificacaoRepository.findById(notificacaoId)).thenReturn(Optional.of(notificacao));
        when(notificacaoRepository.save(any(Notificacao.class))).thenReturn(notificacaoLida);

        // Execução do método
        assertDoesNotThrow(() -> notificacaoService.marcarNotificacaoComoLida(notificacaoId));

        // Verificações
        verify(notificacaoRepository, times(1)).save(any(Notificacao.class));
        verify(kafkaProducerService, times(1)).sendMessage(eq("notificacao-events"), any(NotificacaoResponseDTO.class));
    }

    @Test
    public void marcarNotificacaoComoLida_NaoEncontrada() {
        // Dados de teste
        Long notificacaoId = 1L;

        when(notificacaoRepository.findById(notificacaoId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> notificacaoService.marcarNotificacaoComoLida(notificacaoId));

        // Verifica que o método save não foi chamado
        verify(notificacaoRepository, never()).save(any(Notificacao.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void excluirNotificacao_Sucesso() {
        // Dados de teste
        Long notificacaoId = 1L;

        Notificacao notificacao = Notificacao.builder()
                .id(notificacaoId)
                .mensagem("Notificação para excluir")
                .dataHora(LocalDateTime.now())
                .lida(false)
                .usuario(usuario)
                .build();

        when(notificacaoRepository.findById(notificacaoId)).thenReturn(Optional.of(notificacao));

        // Execução do método
        assertDoesNotThrow(() -> notificacaoService.excluirNotificacao(notificacaoId));

        // Verificações
        verify(notificacaoRepository, times(1)).delete(notificacao);
        verify(kafkaProducerService, times(1)).sendMessage(eq("notificacao-events"), any(NotificacaoResponseDTO.class));
    }

    @Test
    public void excluirNotificacao_NaoEncontrada() {
        // Dados de teste
        Long notificacaoId = 1L;

        when(notificacaoRepository.findById(notificacaoId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> notificacaoService.excluirNotificacao(notificacaoId));

        // Verifica que o método delete não foi chamado
        verify(notificacaoRepository, never()).delete(any(Notificacao.class));

        // Verifica que o evento Kafka não foi enviado
        verify(kafkaProducerService, never()).sendMessage(anyString(), any());
    }

    @Test
    public void contarNotificacoesNaoLidas_Sucesso() {
        // Dados de teste
        Long usuarioId = usuario.getId();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(notificacaoRepository.countByUsuarioAndLida(usuario, false)).thenReturn(5L);

        // Execução do método
        long naoLidas = notificacaoService.contarNotificacoesNaoLidas(usuarioId);

        // Verificações
        assertEquals(5L, naoLidas);
    }

    @Test
    public void contarNotificacoesNaoLidas_UsuarioNaoEncontrado() {
        // Dados de teste
        Long usuarioId = usuario.getId();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> notificacaoService.contarNotificacoesNaoLidas(usuarioId));

        // Verifica que o método count não foi chamado
        verify(notificacaoRepository, never()).countByUsuarioAndLida(any(Usuario.class), anyBoolean());
    }
}
