package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Recomendacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.RecomendacaoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Validated
public class RecomendacaoService {

    private static final Logger logger = LoggerFactory.getLogger(RecomendacaoService.class);

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AzureOpenAiChatModel chatModel;
    private final KafkaTemplate<String, RecomendacaoResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topic.recomendacao-events}")
    private String recomendacaoEventsTopic;

    public RecomendacaoService(RecomendacaoRepository recomendacaoRepository,
                               UsuarioRepository usuarioRepository,
                               AzureOpenAiChatModel chatModel,
                               KafkaTemplate<String, RecomendacaoResponseDTO> kafkaTemplate) {
        this.recomendacaoRepository = recomendacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.chatModel = chatModel;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Cria uma nova recomendação para um usuário específico, gerada pela IA e envia um evento ao Kafka.
     *
     * @param requestDTO Dados da nova recomendação.
     * @return RecomendacaoResponseDTO com os dados da recomendação gerada.
     */
    @Transactional
    public RecomendacaoResponseDTO criarRecomendacao(RecomendacaoRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String promptText = generatePromptText(usuario, requestDTO.getMensagem());
        String generatedContent = generateRecommendationContent(promptText);

        Recomendacao recomendacao = Recomendacao.builder()
                .mensagem(generatedContent)
                .dataHora(LocalDateTime.now())
                .usuario(usuario)
                .build();

        recomendacaoRepository.save(recomendacao);

        RecomendacaoResponseDTO response = convertToResponseDTO(recomendacao);
        kafkaTemplate.send(recomendacaoEventsTopic, response);

        return response;
    }

    /**
     * Lista as recomendações de um usuário específico com suporte a paginação.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de RecomendacaoResponseDTO.
     */
    public Page<RecomendacaoResponseDTO> listarRecomendacoesPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return recomendacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Gera o texto do prompt para a IA baseado na mensagem e no contexto do usuário.
     *
     * @param usuario Usuário destinatário.
     * @param mensagem Mensagem base para a recomendação.
     * @return Texto do prompt a ser enviado para a IA.
     */
    private String generatePromptText(Usuario usuario, String mensagem) {
        return String.format("Gere uma recomendação personalizada para ajudar %s a economizar energia. Base: '%s'.",
                usuario.getNome(), mensagem);
    }

    /**
     * Gera o conteúdo de recomendação com a IA utilizando o modelo AzureOpenAiChatModel.
     *
     * @param promptText Texto do prompt enviado para a IA.
     * @return Conteúdo gerado pela IA.
     */
    private String generateRecommendationContent(String promptText) {
        try {
            logger.info("Enviando prompt para IA: {}", promptText);
            UserMessage userMessage = new UserMessage(promptText);
            return chatModel.call(userMessage);
        } catch (Exception e) {
            logger.error("Erro ao gerar conteúdo de recomendação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar conteúdo de recomendação", e);
        }
    }

    /**
     * Converte uma entidade Recomendacao para RecomendacaoResponseDTO.
     *
     * @param recomendacao Entidade Recomendacao a ser convertida.
     * @return RecomendacaoResponseDTO correspondente.
     */
    private RecomendacaoResponseDTO convertToResponseDTO(Recomendacao recomendacao) {
        return RecomendacaoResponseDTO.builder()
                .id(recomendacao.getId())
                .mensagem(recomendacao.getMensagem())
                .dataHora(recomendacao.getDataHora())
                .usuarioId(recomendacao.getUsuario().getId())
                .build();
    }
}
