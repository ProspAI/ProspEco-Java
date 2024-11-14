package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.service.CustomUserDetailsService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final DataSource dataSource;

    /**
     * Define o PasswordEncoder a ser utilizado na aplicação.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração do SecurityFilterChain para o Authorization Server.
     */
    @Bean
    @Order(1) // Alta prioridade
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Aplica as configurações padrão do Authorization Server
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    /**
     * Configuração consolidada do SecurityFilterChain para a aplicação.
     */
    @Bean
    @Order(2) // Menor prioridade
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desativa CSRF para APIs RESTful
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso público para criação de usuários
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        // Permite acesso público para endpoints públicos e actuator
                        .requestMatchers("/public/**", "/actuator/**").permitAll()
                        // Requer autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                )
                // Configura o login via formulário
                .formLogin(withDefaults());

        return http.build();
    }

    /**
     * Configuração do RegisteredClientRepository para armazenar os clientes OAuth2.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcOperations);

        // Verifica se o cliente já existe no banco de dados
        RegisteredClient existingClient = registeredClientRepository.findByClientId("prospeco-client");
        if (existingClient == null) {
            // Cria e salva um novo cliente registrado
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("prospeco-client")
                    .clientSecret(passwordEncoder.encode("secret")) // Use um PasswordEncoder adequado
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri("http://localhost:8080/login/oauth2/code/prospeco-client")
                    .scope(OidcScopes.OPENID)
                    .scope("read")
                    .scope("write")
                    .build();

            registeredClientRepository.save(registeredClient);
        }

        return registeredClientRepository;
    }

    /**
     * Configuração da fonte de chaves JWK para assinatura dos tokens JWT.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // Gera ou carrega sua chave RSA
        RSAKey rsaKey = generateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * Método auxiliar para gerar uma chave RSA.
     */
    private static RSAKey generateRsaKey() {
        KeyPair keyPair = generateRsaKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    /**
     * Gera um par de chaves RSA.
     */
    private static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Configuração do JwtDecoder para decodificar e validar os tokens JWT.
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Configuração do AuthorizationServerSettings.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * Configuração do UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }
}
