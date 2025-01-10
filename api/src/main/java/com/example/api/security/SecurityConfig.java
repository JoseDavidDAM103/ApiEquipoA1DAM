package com.example.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Bean para encriptar contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Autenticación manager builder
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsServiceImpl userDetailsService) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

        return authBuilder.build();
    }


    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1) Desactivar CSRF en estilo lambda
        http.csrf(csrf -> csrf.disable());

        // 2) Configuración de autorización usando lambdas
        http.authorizeHttpRequests(auth -> auth
                // Permitir sin token el endpoint de login, registro, etc.
                .requestMatchers("/api/auth/**").permitAll()
                // Permitir sin token la documentación de Swagger (opcional)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // El resto de endpoints requieren autenticación
                .anyRequest().authenticated()
        );

        // 3) Agregar tu filtro JWT antes del UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 4) Finalmente, construimos el objeto
        return http.build();
    }

}

