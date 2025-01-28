package com.example.api.repositories;

import com.example.api.models.Grupo;
import com.example.api.models.GrupoParticipante;
import org.springframework.data.repository.ListCrudRepository;

public interface GrupoParticipanteRepository extends ListCrudRepository<GrupoParticipante, Integer> {
    Long countAllByGrupo(Grupo grupo);
}