// FirebaseTokenFilter.java
package br.com.fiap.jadv.prospeco.config;

import br.com.fiap.jadv.prospeco.model.Usuario;
import br.com.fiap.jadv.prospeco.service.CustomUserDetailsService;
import br.com.fiap.jadv.prospeco.service.UsuarioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseTokenFilter.class);

    private final UsuarioService usuarioService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String email = decodedToken.getEmail();

                if (email != null) {
                    // Verifique se o usuário já existe no banco de dados
                    Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
                    if (usuario == null) {
                        // Registre o usuário no banco de dados se ele não existir
                        usuario = usuarioService.registrarUsuarioFirebase(decodedToken.getUid(), decodedToken.getName(), email);
                    }

                    // Carregar detalhes do usuário para autenticação
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getEmail());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (FirebaseAuthException e) {
                logger.error("Erro ao verificar o token do Firebase: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido ou expirado.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
