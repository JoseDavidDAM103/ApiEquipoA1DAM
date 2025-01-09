package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grupos-participantes")
public class GruposParticipanteController {

    @Autowired
    private GruposParticipantesRepository gruposParticipantesRepository;

    @GetMapping
    public List<GruposParticipantes> getAllGruposParticipantes() {
        return gruposParticipantesRepository.findAll();
    }

    @GetMapping("/{id}")
    public GruposParticipantes getGruposParticipantesById(@PathVariable Integer id) {
        Optional<GruposParticipantes> gp = gruposParticipantesRepository.findById(id);
        return gp.orElse(null);
    }

    @PostMapping
    public GruposParticipantes createGruposParticipantes(@RequestBody GruposParticipantes nuevoGP) {
        return gruposParticipantesRepository.save(nuevoGP);
    }

    @PutMapping("/{id}")
    public GruposParticipantes updateGruposParticipantes(@PathVariable Integer id, @RequestBody GruposParticipantes gpActualizado) {
        return gruposParticipantesRepository.findById(id)
                .map(gp -> {
                    gp.setActividadesId(gpActualizado.getActividadesId());
                    gp.setGrupoId(gpActualizado.getGrupoId());
                    gp.setNumParticipantes(gpActualizado.getNumParticipantes());
                    gp.setComentario(gpActualizado.getComentario());
                    return gruposParticipantesRepository.save(gp);
                })
                .orElseGet(() -> {
                    gpActualizado.setId(id);
                    return gruposParticipantesRepository.save(gpActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteGruposParticipantes(@PathVariable Integer id) {
        gruposParticipantesRepository.deleteById(id);
    }
}

