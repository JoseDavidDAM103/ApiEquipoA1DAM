package com.example.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener cabecera "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Comprobar si la cabecera tiene la forma "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                // 3. Extraer el username
                username = jwtUtils.getUsernameFromToken(token);
            } catch (Exception e) {
                System.out.println("Error al parsear el token JWT: " + e.getMessage());
            }
        }

        // 4. Validar token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargar el usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Verificar que el token es válido
            if (jwtUtils.validateToken(token, userDetails)) {
                // Crear objeto de autenticación y asignarlo al contexto
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}

