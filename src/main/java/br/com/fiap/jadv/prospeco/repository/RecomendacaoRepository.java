package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Recomendacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * <h1>RecomendacaoRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade Recomendacao.
 */
public interface RecomendacaoRepository extends JpaRepository<Recomendacao, Long> {

    /**
     * Busca as recomendações de um usuário, ordenadas pela data e hora.
     *
     * @param usuario Usuário que recebeu as recomendações.
     * @return Lista de recomendações do usuário.
     */
    List<Recomendacao> findByUsuarioOrderByDataHoraDesc(Usuario usuario);
}