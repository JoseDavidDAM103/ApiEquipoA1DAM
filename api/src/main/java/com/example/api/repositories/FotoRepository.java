package com.example.api.repositories;

import com.example.api.models.Foto;
import org.springframework.data.repository.ListCrudRepository;

public interface FotoRepository extends ListCrudRepository<Foto, Integer> {
}