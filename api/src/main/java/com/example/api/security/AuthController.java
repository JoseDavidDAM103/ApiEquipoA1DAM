package com.example.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // Crear objeto de autenticación
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(), authRequest.getPassword());

        // Autenticar
        try {
            authenticationManager.authenticate(authInputToken);

            // Si la autenticación es correcta, cargamos el usuario
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            // Generamos el token
            final String jwt = jwtUtils.generateToken(userDetails);

            // Retornamos el token en la respuesta
            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    // Clases internas (DTOs) para request/response de login
    public static class AuthRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    public static class AuthResponse {
        private String token;
        public AuthResponse(String token) {
            this.token = token;
        }
        // Getter
        public String getToken() {
            return token;
        }
    }
}

