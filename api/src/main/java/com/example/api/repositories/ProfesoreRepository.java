package com.example.api.repositories;

import com.example.api.models.Profesore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ProfesoreRepository extends JpaRepository<Profesore, String> {
    Optional<Profesore> findProfesoresByDni(String dni);
}