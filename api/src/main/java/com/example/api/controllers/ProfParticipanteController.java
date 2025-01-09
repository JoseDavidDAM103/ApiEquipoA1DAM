package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prof-participantes")
public class ProfParticipanteController {

    @Autowired
    private ProfParticipantesRepository profParticipantesRepository;

    @GetMapping
    public List<ProfParticipantes> getAllProfParticipantes() {
        return profParticipantesRepository.findAll();
    }

    @GetMapping("/{id}")
    public ProfParticipantes getProfParticipantesById(@PathVariable Integer id) {
        Optional<ProfParticipantes> pp = profParticipantesRepository.findById(id);
        return pp.orElse(null);
    }

    @PostMapping
    public ProfParticipantes createProfParticipantes(@RequestBody ProfParticipantes nuevoPP) {
        return profParticipantesRepository.save(nuevoPP);
    }

    @PutMapping("/{id}")
    public ProfParticipantes updateProfParticipantes(@PathVariable Integer id, @RequestBody ProfParticipantes ppActualizado) {
        return profParticipantesRepository.findById(id)
                .map(pp -> {
                    pp.setActividadId(ppActualizado.getActividadId());
                    pp.setProfesorId(ppActualizado.getProfesorId());
                    return profParticipantesRepository.save(pp);
                })
                .orElseGet(() -> {
                    ppActualizado.setId(id);
                    return profParticipantesRepository.save(ppActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteProfParticipantes(@PathVariable Integer id) {
        profParticipantesRepository.deleteById(id);
    }
}

