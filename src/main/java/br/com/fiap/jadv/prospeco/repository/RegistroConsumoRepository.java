package br.com.fiap.jadv.prospeco.repository;


import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.RegistroConsumo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

/**
 * <h1>RegistroConsumoRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade RegistroConsumo.
 */
public interface RegistroConsumoRepository extends JpaRepository<RegistroConsumo, Long> {

    /**
     * Busca os registros de consumo de um determinado aparelho, com suporte a paginação.
     *
     * @param aparelho Aparelho cujos registros serão buscados.
     * @return Página contendo os registros de consumo.
     */
    Page<RegistroConsumo> findByAparelho(Aparelho aparelho, Pageable pageable);
}
