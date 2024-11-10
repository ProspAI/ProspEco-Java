package br.com.fiap.jadv.prospeco.repository;

import br.com.fiap.jadv.prospeco.model.Conquista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConquistaRepository extends JpaRepository<Conquista, Long> {

    // Método com paginação por ID do usuário
    Page<Conquista> findByUsuarioId(Long usuarioId, Pageable pageable);
}
