package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Recomendacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.RecomendacaoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RecomendacaoService {

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AzureOpenAiChatModel chatModel;

    @Autowired
    public RecomendacaoService(RecomendacaoRepository recomendacaoRepository,
                               UsuarioRepository usuarioRepository,
                               AzureOpenAiChatModel chatModel) {
        this.recomendacaoRepository = recomendacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.chatModel = chatModel;
    }

    public Page<RecomendacaoResponseDTO> listarRecomendacoesPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return recomendacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)
                .map(this::toResponseDTO);
    }

    public RecomendacaoResponseDTO criarRecomendacao(RecomendacaoRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String promptText = generatePromptText(requestDTO.getAparelho());

        String mensagemGerada = generateEnergySavingRecommendation(promptText);

        Recomendacao recomendacao = Recomendacao.builder()
                .mensagem(mensagemGerada)
                .dataHora(LocalDateTime.now())
                .usuario(usuario)
                .build();

        Recomendacao novaRecomendacao = recomendacaoRepository.save(recomendacao);

        return toResponseDTO(novaRecomendacao);
    }

    private String generatePromptText(String aparelho) {
        return String.format("Gere uma recomendação detalhada para economizar energia ao usar o aparelho '%s'.", aparelho);
    }

    private String generateEnergySavingRecommendation(String promptText) {
        UserMessage userMessage = new UserMessage(promptText);
        return chatModel.call(userMessage);
    }

    private RecomendacaoResponseDTO toResponseDTO(Recomendacao recomendacao) {
        return RecomendacaoResponseDTO.builder()
                .id(recomendacao.getId())
                .mensagem(recomendacao.getMensagem())
                .dataHora(recomendacao.getDataHora())
                .usuarioId(recomendacao.getUsuario().getId())
                .build();
    }
}
