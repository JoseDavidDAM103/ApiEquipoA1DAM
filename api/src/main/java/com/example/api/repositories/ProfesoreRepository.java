package com.example.api.repositories;

import com.example.api.models.Profesore;
import org.springframework.data.repository.ListCrudRepository;

public interface ProfesoreRepository extends ListCrudRepository<Profesore, String> {
}