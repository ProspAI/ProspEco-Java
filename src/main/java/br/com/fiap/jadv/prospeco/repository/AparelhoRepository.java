package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Aparelho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AparelhoRepository extends JpaRepository<Aparelho, Long> {

    /**
     * Busca aparelhos associados a um usuário específico com suporte a paginação.
     *
     * @param usuarioId ID do usuário proprietário dos aparelhos.
     * @param pageable  Objeto de paginação.
     * @return Página contendo os aparelhos do usuário.
     */
    Page<Aparelho> findByUsuarioId(Long usuarioId, Pageable pageable);
}
