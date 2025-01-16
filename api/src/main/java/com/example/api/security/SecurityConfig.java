package com.example.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtValidators;


@EnableWebSecurity
@Configuration
public class SecurityConfig {


    // Configuración principal de seguridad
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                // En lugar de oauth2.jwt() directo, usamos el Customizer
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Define el URI de la JWK Set de Azure
        String jwkSetUri = "https://login.microsoftonline.com/common/discovery/v2.0/keys";

        // Construye el decoder
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Validador predeterminado que valida exp y nbf
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefault();

        // Validador personalizado para omitir la validación de 'iss'
        OAuth2TokenValidator<Jwt> withoutIssuerValidator = jwt -> OAuth2TokenValidatorResult.success();

        // Combina los validadores usando DelegatingOAuth2TokenValidator
        OAuth2TokenValidator<Jwt> combinedValidators =
                new DelegatingOAuth2TokenValidator<>(defaultValidators, withoutIssuerValidator);

        // Establece los validadores combinados
        jwtDecoder.setJwtValidator(combinedValidators);

        return jwtDecoder;
    }

}

