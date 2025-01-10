package com.example.api.controllers;

import com.example.api.models.GruposParticipante;
import com.example.api.repositories.GruposParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gruposParticipantes")
public class GruposParticipanteController {

    @Autowired
    private GruposParticipanteRepository GruposParticipanteRepository;

    @GetMapping
    public List<GruposParticipante> getAllGruposParticipante() {
        return GruposParticipanteRepository.findAll();
    }

    @GetMapping("/{id}")
    public GruposParticipante getGruposParticipanteById(@PathVariable Integer id) {
        Optional<GruposParticipante> gp = GruposParticipanteRepository.findById(id);
        return gp.orElse(null);
    }

    @PostMapping
    public GruposParticipante createGruposParticipante(@RequestBody GruposParticipante nuevoGP) {
        return GruposParticipanteRepository.save(nuevoGP);
    }

    @PutMapping("/{id}")
    public GruposParticipante updateGruposParticipante(@PathVariable Integer id, @RequestBody GruposParticipante gpActualizado) {
        return GruposParticipanteRepository.findById(id)
                .map(gp -> {
                    gp.setActividades(gpActualizado.getActividades());
                    gp.setGrupo(gpActualizado.getGrupo());
                    gp.setNumParticipantes(gpActualizado.getNumParticipantes());
                    gp.setComentario(gpActualizado.getComentario());
                    return GruposParticipanteRepository.save(gp);
                })
                .orElseGet(() -> {
                    gpActualizado.setId(id);
                    return GruposParticipanteRepository.save(gpActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteGruposParticipante(@PathVariable Integer id) {
        GruposParticipanteRepository.deleteById(id);
    }
}

