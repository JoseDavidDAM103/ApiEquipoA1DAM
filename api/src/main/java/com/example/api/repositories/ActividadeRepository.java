package com.example.api.repositories;

import com.example.api.models.Actividade;
import org.springframework.data.repository.ListCrudRepository;

public interface ActividadeRepository extends ListCrudRepository<Actividade, Integer> {
}