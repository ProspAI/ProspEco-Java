package br.com.fiap.jadv.prospeco.controller.Mvc;

import br.com.fiap.jadv.prospeco.dto.request.RecomendacaoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.RecomendacaoResponseDTO;
import br.com.fiap.jadv.prospeco.service.RecomendacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recomendacoes")
public class RecomendacaoMvcController {

    private final RecomendacaoService recomendacaoService;

    @Autowired
    public RecomendacaoMvcController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    /**
     * Lista todas as recomendações de um usuário.
     *
     * @param usuarioId ID do usuário.
     * @param pageable  Informações de paginação.
     * @param model     Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/usuario/{usuarioId}")
    public String listarRecomendacoesPorUsuario(@PathVariable Long usuarioId,
                                                Pageable pageable,
                                                Model model) {
        Page<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.listarRecomendacoesPorUsuario(usuarioId, pageable);
        model.addAttribute("recomendacoes", recomendacoes);
        model.addAttribute("usuarioId", usuarioId);
        return "recomendacoes/list";
    }

    /**
     * Exibe o formulário para criação de uma nova recomendação.
     *
     * @param usuarioId ID do usuário.
     * @param model     Modelo para a view.
     * @return Nome da página Thymeleaf.
     */
    @GetMapping("/usuario/{usuarioId}/nova")
    public String mostrarFormCriarRecomendacao(@PathVariable Long usuarioId, Model model) {
        RecomendacaoRequestDTO recomendacao = new RecomendacaoRequestDTO();
        recomendacao.setUsuarioId(usuarioId);
        model.addAttribute("recomendacao", recomendacao);
        model.addAttribute("usuarioId", usuarioId);
        return "recomendacoes/create";
    }

    /**
     * Processa o formulário de criação de uma nova recomendação.
     *
     * @param usuarioId  ID do usuário.
     * @param requestDTO Dados da recomendação a ser criada.
     * @param result     Resultado da validação.
     * @param model      Modelo para a view.
     * @return Redirecionamento ou página do formulário se houver erros.
     */
    @PostMapping("/usuario/{usuarioId}")
    public String criarRecomendacao(@PathVariable Long usuarioId,
                                    @Valid @ModelAttribute("recomendacao") RecomendacaoRequestDTO requestDTO,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", usuarioId);
            return "recomendacoes/create";
        }
        requestDTO.setUsuarioId(usuarioId);
        recomendacaoService.criarRecomendacao(requestDTO);
        return "redirect:/recomendacoes/usuario/" + usuarioId;
    }
}
