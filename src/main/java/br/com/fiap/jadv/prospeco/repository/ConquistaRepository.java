package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Conquista;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * <h1>ConquistaRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade Conquista.
 */
public interface ConquistaRepository extends JpaRepository<Conquista, Long> {

    /**
     * Busca todas as conquistas de um usuário.
     *
     * @param usuario Usuário que obteve as conquistas.
     * @return Lista de conquistas do usuário.
     */
    List<Conquista> findByUsuario(Usuario usuario);
}
