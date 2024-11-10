package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * <h1>UsuarioRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade Usuario.
 * Estende JpaRepository para herdar métodos padrão de CRUD.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário.
     * @return Optional contendo o usuário, se encontrado.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se um usuário com o email especificado existe.
     *
     * @param email Email do usuário.
     * @return true se existir, false caso contrário.
     */
    boolean existsByEmail(String email);
}
