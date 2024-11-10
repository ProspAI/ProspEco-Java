package br.com.fiap.jadv.prospeco.repository;


import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * <h1>AparelhoRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade Aparelho.
 */
public interface AparelhoRepository extends JpaRepository<Aparelho, Long> {

    /**
     * Busca todos os aparelhos pertencentes a um usuário.
     *
     * @param usuario Usuário proprietário dos aparelhos.
     * @return Lista de aparelhos do usuário.
     */
    List<Aparelho> findByUsuario(Usuario usuario);

    /**
     * Verifica se um aparelho com determinado nome já existe para um usuário.
     *
     * @param nome Nome do aparelho.
     * @param usuario Usuário proprietário.
     * @return true se existir, false caso contrário.
     */
    boolean existsByNomeAndUsuario(String nome, Usuario usuario);
}
