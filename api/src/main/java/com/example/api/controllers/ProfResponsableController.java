package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prof-responsables")
public class ProfResponsableController {

    @Autowired
    private ProfResponsablesRepository profResponsablesRepository;

    @GetMapping
    public List<ProfResponsables> getAllProfResponsables() {
        return profResponsablesRepository.findAll();
    }

    @GetMapping("/{id}")
    public ProfResponsables getProfResponsableById(@PathVariable Integer id) {
        Optional<ProfResponsables> pr = profResponsablesRepository.findById(id);
        return pr.orElse(null);
    }

    @PostMapping
    public ProfResponsables createProfResponsable(@RequestBody ProfResponsables nuevoPR) {
        return profResponsablesRepository.save(nuevoPR);
    }

    @PutMapping("/{id}")
    public ProfResponsables updateProfResponsable(@PathVariable Integer id, @RequestBody ProfResponsables prActualizado) {
        return profResponsablesRepository.findById(id)
                .map(pr -> {
                    pr.setActividadId(prActualizado.getActividadId());
                    pr.setProfesorId(prActualizado.getProfesorId());
                    return profResponsablesRepository.save(pr);
                })
                .orElseGet(() -> {
                    prActualizado.setId(id);
                    return profResponsablesRepository.save(prActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteProfResponsable(@PathVariable Integer id) {
        profResponsablesRepository.deleteById(id);
    }
}

