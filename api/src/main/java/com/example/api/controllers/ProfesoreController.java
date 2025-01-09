package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profesores")
public class ProfesoreController {

    @Autowired
    private ProfesoresRepository profesoresRepository;

    @GetMapping
    public List<Profesores> getAllProfesores() {
        return profesoresRepository.findAll();
    }

    @GetMapping("/{uuid}")
    public Profesores getProfesorByUuid(@PathVariable String uuid) {
        Optional<Profesores> profesor = profesoresRepository.findById(uuid);
        return profesor.orElse(null);
    }

    @PostMapping
    public Profesores createProfesor(@RequestBody Profesores nuevoProfesor) {
        return profesoresRepository.save(nuevoProfesor);
    }

    @PutMapping("/{uuid}")
    public Profesores updateProfesor(@PathVariable String uuid, @RequestBody Profesores profesorActualizado) {
        return profesoresRepository.findById(uuid)
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
                    profesor.setDepartId(profesorActualizado.getDepartId());
                    return profesoresRepository.save(profesor);
                })
                .orElseGet(() -> {
                    profesorActualizado.setUuid(uuid);
                    return profesoresRepository.save(profesorActualizado);
                });
    }

    @DeleteMapping("/{uuid}")
    public void deleteProfesor(@PathVariable String uuid) {
        profesoresRepository.deleteById(uuid);
    }
}

