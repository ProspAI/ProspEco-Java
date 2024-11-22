package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseTokenFilter firebaseTokenFilter; // Filtro para autenticação Firebase
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Filtro para autenticação JWT
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Configuração de segurança para APIs.
     * Usa autenticação stateless com suporte para Firebase e JWT.
     */
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/auth/**")
                .csrf(csrf -> csrf.disable()) // CSRF não é necessário para APIs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitir acesso a todos os endpoints da API
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessões stateless para APIs
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Filtro JWT
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class) // Filtro Firebase
                .userDetailsService(customUserDetailsService); // Custom UserDetailsService

        return http.build();
    }

    /**
     * Configuração de segurança para páginas web.
     * Usa autenticação baseada em sessão com suporte para login de formulário.
     */
    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable()) // CSRF pode ser habilitado para maior segurança
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll() // Acesso público
                        .anyRequest().authenticated() // Todas as outras rotas precisam de autenticação
                )
                .formLogin(form -> form
                        .loginPage("/login") // Página de login personalizada
                        .loginProcessingUrl("/login") // URL para processamento do login
                        .defaultSuccessUrl("/usuarios", true) // Redirecionamento após sucesso no login
                        .permitAll() // Permitir acesso ao login para todos
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para logout
                        .logoutSuccessUrl("/login?logout") // Redirecionamento após logout
                        .permitAll() // Permitir acesso ao logout para todos
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) // Sessões persistentes
                .authenticationProvider(authenticationProvider()) // Autenticação personalizada
                .userDetailsService(customUserDetailsService); // Custom UserDetailsService

        return http.build();
    }

    /**
     * Provedor de autenticação para autenticação baseada em sessão.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // Custom UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Codificador de senhas
        return authProvider;
    }

    /**
     * Bean de codificador de senhas usando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Gerenciador de autenticação necessário para autenticação programática e APIs.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
