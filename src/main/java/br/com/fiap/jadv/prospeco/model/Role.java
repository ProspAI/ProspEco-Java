package br.com.fiap.jadv.prospeco.model;

/**
 * <h1>Role</h1>
 * Enumeração que define os papéis (roles) que um usuário
 * pode possuir no sistema. Utilizado para controle de acesso
 * e autorização com o Spring Security.
 *
 * <ul>
 *   <li>ROLE_USER: Usuário padrão do sistema.</li>
 *   <li>ROLE_ADMIN: Administrador com privilégios especiais.</li>
 * </ul>
 *
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
