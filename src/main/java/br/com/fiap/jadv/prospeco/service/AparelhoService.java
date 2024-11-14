package br.com.fiap.jadv.prospeco.service;

import br.com.fiap.jadv.prospeco.dto.request.AparelhoRequestDTO;
import br.com.fiap.jadv.prospeco.dto.response.AparelhoResponseDTO;
import br.com.fiap.jadv.prospeco.exception.ResourceNotFoundException;
import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.repository.AparelhoRepository;
import br.com.fiap.jadv.prospeco.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class AparelhoService {

    private final AparelhoRepository aparelhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Cria um novo aparelho para o usuário especificado.
     *
     * @param requestDTO Dados de requisição do aparelho.
     * @param usuarioId  Identificador do usuário proprietário do aparelho.
     * @return AparelhoResponseDTO contendo os dados do aparelho criado.
     */
    @Transactional
    public AparelhoResponseDTO criarAparelho(AparelhoRequestDTO requestDTO, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Aparelho aparelho = Aparelho.builder()
                .nome(requestDTO.getNome())
                .potencia(requestDTO.getPotencia())
                .tipo(requestDTO.getTipo())
                .descricao(requestDTO.getDescricao())
                .usuario(usuario)
                .build();

        aparelhoRepository.save(aparelho);

        AparelhoResponseDTO response = convertToResponseDTO(aparelho);
        kafkaProducerService.sendAparelhoEvent(response);

        return response;
    }

    /**
     * Atualiza um aparelho existente.
     *
     * @param id         Identificador do aparelho a ser atualizado.
     * @param requestDTO Dados de atualização do aparelho.
     * @return AparelhoResponseDTO contendo os dados do aparelho atualizado.
     */
    @Transactional
    public AparelhoResponseDTO atualizarAparelho(Long id, AparelhoRequestDTO requestDTO) {
        Aparelho aparelho = aparelhoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        BeanUtils.copyProperties(requestDTO, aparelho, "id", "usuario");

        aparelhoRepository.save(aparelho);

        AparelhoResponseDTO response = convertToResponseDTO(aparelho);
        kafkaProducerService.sendAparelhoEvent(response);

        return response;
    }

    /**
     * Lista aparelhos de um usuário com paginação.
     *
     * @param usuarioId Identificador do usuário.
     * @param pageable  Configuração de paginação.
     * @return Página de AparelhoResponseDTO.
     */
    public Page<AparelhoResponseDTO> listarAparelhosPorUsuario(Long usuarioId, Pageable pageable) {
        return aparelhoRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Exclui um aparelho por ID.
     *
     * @param id Identificador do aparelho a ser excluído.
     */
    @Transactional
    public void excluirAparelho(Long id) {
        Aparelho aparelho = aparelhoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aparelho não encontrado"));

        aparelhoRepository.delete(aparelho);
    }

    /**
     * Converte um Aparelho em AparelhoResponseDTO.
     *
     * @param aparelho Aparelho a ser convertido.
     * @return AparelhoResponseDTO correspondente.
     */
    private AparelhoResponseDTO convertToResponseDTO(Aparelho aparelho) {
        return AparelhoResponseDTO.builder()
                .id(aparelho.getId())
                .nome(aparelho.getNome())
                .potencia(aparelho.getPotencia())
                .tipo(aparelho.getTipo())
                .descricao(aparelho.getDescricao())
                .usuarioId(aparelho.getUsuario().getId())
                .build();
    }
}
