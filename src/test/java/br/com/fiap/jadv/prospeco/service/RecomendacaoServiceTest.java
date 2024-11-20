package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Recomendacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.RecomendacaoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RecomendacaoServiceTest {

    @Mock
    private RecomendacaoRepository recomendacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AzureOpenAiChatModel chatModel;

    @InjectMocks
    private RecomendacaoService recomendacaoService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        // Configura o chatModel para retornar uma mensagem simulada
        when(chatModel.call(any(UserMessage.class))).thenReturn("Mensagem de recomendação simulada.");
    }

    @Test
    public void listarRecomendacoesPorUsuario_Sucesso() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        Recomendacao recomendacao1 = Recomendacao.builder()
                .id(1L)
                .mensagem("Recomendação 1")
                .dataHora(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Recomendacao recomendacao2 = Recomendacao.builder()
                .id(2L)
                .mensagem("Recomendação 2")
                .dataHora(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Page<Recomendacao> recomendacoesPage = new PageImpl<>(Arrays.asList(recomendacao1, recomendacao2), pageable, 2);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(recomendacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)).thenReturn(recomendacoesPage);

        // Execução do método
        Page<RecomendacaoResponseDTO> result = recomendacaoService.listarRecomendacoesPorUsuario(usuarioId, pageable);

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Recomendação 1", result.getContent().get(0).getMensagem());
        assertEquals("Recomendação 2", result.getContent().get(1).getMensagem());
    }

    @Test
    public void listarRecomendacoesPorUsuario_UsuarioNaoEncontrado() {
        // Dados de teste
        Long usuarioId = usuario.getId();
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> recomendacaoService.listarRecomendacoesPorUsuario(usuarioId, pageable));

        // Verifica que o repositório de recomendações não foi chamado
        verify(recomendacaoRepository, never()).findByUsuarioOrderByDataHoraDesc(any(Usuario.class), any(Pageable.class));
    }

    @Test
    public void criarRecomendacao_Sucesso() {
        // Dados de teste
        RecomendacaoRequestDTO requestDTO = RecomendacaoRequestDTO.builder()
                .aparelho("Geladeira")
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Recomendacao recomendacaoSalva = Recomendacao.builder()
                .id(1L)
                .mensagem("Mensagem de recomendação simulada.")
                .dataHora(LocalDateTime.now())
                .usuario(usuario)
                .build();

        when(recomendacaoRepository.save(any(Recomendacao.class))).thenReturn(recomendacaoSalva);

        // Execução do método
        RecomendacaoResponseDTO result = recomendacaoService.criarRecomendacao(requestDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mensagem de recomendação simulada.", result.getMensagem());
        assertEquals(usuario.getId(), result.getUsuarioId());

        // Verifica se o chatModel foi chamado corretamente
        verify(chatModel, times(1)).call(any(UserMessage.class));
    }

    @Test
    public void criarRecomendacao_UsuarioNaoEncontrado() {
        // Dados de teste
        RecomendacaoRequestDTO requestDTO = RecomendacaoRequestDTO.builder()
                .aparelho("Geladeira")
                .usuarioId(usuario.getId())
                .build();

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        // Execução e verificação
        assertThrows(ResourceNotFoundException.class, () -> recomendacaoService.criarRecomendacao(requestDTO));

        // Verifica que o método save não foi chamado
        verify(recomendacaoRepository, never()).save(any(Recomendacao.class));

        // Verifica que o chatModel não foi chamado
        verify(chatModel, never()).call(any(UserMessage.class));
    }
}
