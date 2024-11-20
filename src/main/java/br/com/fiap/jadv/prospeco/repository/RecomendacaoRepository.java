package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Recomendacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
    Page<Recomendacao> findByUsuarioOrderByDataHoraDesc(Usuario usuario, Pageable pageable);
}
