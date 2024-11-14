package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AparelhoRepository extends JpaRepository<Aparelho, Long> {

    Page<Aparelho> findByUsuario(Usuario usuario, Pageable pageable);

    boolean existsByNomeAndUsuario(String nome, Usuario usuario);
}
