package com.example.api.controllers;

import com.example.api.models.Profesore;
import com.example.api.repositories.ProfesoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/Profesore")
public class ProfesoreController {

    @Autowired
    private ProfesoreRepository ProfesoreRepository;

    @GetMapping
    public List<Profesore> getAllProfesore() {
        return ProfesoreRepository.findAll();
    }

    @GetMapping("/{uuid}")
    public Profesore getProfesorByUuid(@PathVariable String uuid) {
        Optional<Profesore> profesor = ProfesoreRepository.findById(uuid);
        return profesor.orElse(null);
    }

    @GetMapping("/dni/{dni}")
    public Profesore getProfesorByDni(@PathVariable String dni) {
        Optional<Profesore> profesor = ProfesoreRepository.findProfesoresByDni(dni);
        return profesor.orElse(null);
    }

    @PostMapping
    public Profesore createProfesor(@RequestBody Profesore nuevoProfesor) {
        return ProfesoreRepository.save(nuevoProfesor);
    }

    @PutMapping("/{uuid}")
    public Profesore updateProfesor(@PathVariable String uuid, @RequestBody Profesore profesorActualizado) {
        return ProfesoreRepository.findById(uuid)
                .map(profesor -> {
                    profesor.setDni(profesorActualizado.getDni());
                    profesor.setNombre(profesorActualizado.getNombre());
                    profesor.setApellidos(profesorActualizado.getApellidos());
                    profesor.setCorreo(profesorActualizado.getCorreo());
                    profesor.setPassword(profesorActualizado.getPassword());
                    profesor.setRol(profesorActualizado.getRol());
                    profesor.setUrlFoto(profesorActualizado.getUrlFoto());
                    profesor.setActivo(profesorActualizado.getActivo());
                    profesor.setEsJefeDep(profesorActualizado.getEsJefeDep());
                    profesor.setDepart(profesorActualizado.getDepart());
                    return ProfesoreRepository.save(profesor);
                })
                .orElseGet(() -> {
                    profesorActualizado.setUuid(uuid);
                    return ProfesoreRepository.save(profesorActualizado);
                });
    }

    @DeleteMapping("/{uuid}")
    public void deleteProfesor(@PathVariable String uuid) {
        ProfesoreRepository.deleteById(uuid);
    }
}

