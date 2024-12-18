package br.com.fiap.jadv.prospeco.repository;


import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>MetaRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade Meta.
 */
public interface MetaRepository extends JpaRepository<Meta, Long> {

    /**
     * Busca todas as metas de um usuário.
     *
     * @param usuario Usuário proprietário das metas.
     * @return Lista de metas do usuário.
     */
    Page<Meta> findByUsuario(Usuario usuario, Pageable pageable);
}
