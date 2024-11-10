package br.com.fiap.jadv.prospeco.repository;


import br.com.fiap.jadv.prospeco.model.Notificacao;
import br.com.fiap.jadv.prospeco.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * <h1>NotificacaoRepository</h1>
 * Interface responsável pelas operações de acesso a dados da entidade Notificacao.
 */
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    /**
     * Busca as notificações de um usuário, ordenadas pela data e hora de envio.
     *
     * @param usuario Usuário que recebeu as notificações.
     * @return Lista de notificações do usuário.
     */
    List<Notificacao> findByUsuarioOrderByDataHoraDesc(Usuario usuario);

    /**
     * Conta o número de notificações não lidas de um usuário.
     *
     * @param usuario Usuário que recebeu as notificações.
     * @param lida Status de leitura (false para não lidas).
     * @return Número de notificações não lidas.
     */
    long countByUsuarioAndLida(Usuario usuario, Boolean lida);
}
