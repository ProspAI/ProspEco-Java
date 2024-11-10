package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.BandeiraTarifaria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

/**
 * <h1>BandeiraTarifariaRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade BandeiraTarifaria.
 */
public interface BandeiraTarifariaRepository extends JpaRepository<BandeiraTarifaria, Long> {

    /**
     * Busca a bandeira tarifária vigente para uma data específica.
     *
     * @param dataVigencia Data para a qual a bandeira será buscada.
     * @return Optional contendo a bandeira tarifária, se encontrada.
     */
    Optional<BandeiraTarifaria> findByDataVigencia(LocalDate dataVigencia);
}
