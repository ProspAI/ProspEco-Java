package br.com.fiap.jadv.prospeco.model;

import br.com.fiap.jadv.prospeco.model.Aparelho;
import br.com.fiap.jadv.prospeco.model.Conquista;
import br.com.fiap.jadv.prospeco.model.Meta;
import br.com.fiap.jadv.prospeco.model.Notificacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import javax.management.relation.Role;
import java.util.Set;
import java.util.HashSet;

/**
 * <h1>Usuario</h1>
 * Classe que representa um usuário do sistema ProspEco.
 * Contém informações de autenticação, perfil e relacionamentos
 * com outras entidades, como aparelhos, metas, notificações e conquistas.
 * <p>
 * Possui validações para garantir a integridade dos dados e utiliza
 * anotações do Lombok para reduzir o código boilerplate.
 * </p>
 *
 * @author
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    /**
     * Identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    /**
     * Endereço de email único do usuário.
     */
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * Senha criptografada do usuário.
     */
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    @Column(nullable = false)
    private String senha;

    /**
     * Papéis (roles) atribuídos ao usuário para controle de acesso.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    /**
     * Pontuação acumulada de economia de energia do usuário.
     */
    @PositiveOrZero(message = "A pontuação de economia deve ser positiva.")
    @Column(name = "pontuacao_economia", nullable = false)
    private Double pontuacaoEconomia = 0.0;

    /**
     * Lista de aparelhos registrados pelo usuário.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Aparelho> aparelhos = new HashSet<>();

    /**
     * Lista de metas definidas pelo usuário.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Meta> metas = new HashSet<>();

    /**
     * Lista de notificações recebidas pelo usuário.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Notificacao> notificacoes = new HashSet<>();

    /**
     * Lista de conquistas obtidas pelo usuário.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Conquista> conquistas = new HashSet<>();
}
