package com.example.api.security;

import com.example.api.models.Profesor;
import com.example.api.repositories.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ProfesorRepository profesorRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Aquí buscamos el usuario en la base de datos
        Profesor profesor = profesorRepository.findProfesorByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        // Convertimos nuestro usuario a un objeto UserDetails
        return new org.springframework.security.core.userdetails.User(
                profesor.getCorreo(),
                profesor.getPassword(),
                Boolean.parseBoolean(profesor.getActivo().toString()),
                true,
                true,
                true,
                getAuthorities(profesor.getRol())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }
}

