package com.example.api.controllers;

package com.example.demo.controllers;

import com.example.demo.entities.Grupos;
import com.example.demo.repositories.GruposRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    @Autowired
    private GruposRepository gruposRepository;

    @GetMapping
    public List<Grupos> getAllGrupos() {
        return gruposRepository.findAll();
    }

    @GetMapping("/{id}")
    public Grupos getGrupoById(@PathVariable Integer id) {
        Optional<Grupos> grupo = gruposRepository.findById(id);
        return grupo.orElse(null);
    }

    @PostMapping
    public Grupos createGrupo(@RequestBody Grupos nuevoGrupo) {
        return gruposRepository.save(nuevoGrupo);
    }

    @PutMapping("/{id}")
    public Grupos updateGrupo(@PathVariable Integer id, @RequestBody Grupos grupoActualizado) {
        return gruposRepository.findById(id)
                .map(grupo -> {
                    grupo.setCursoId(grupoActualizado.getCursoId());
                    grupo.setCodGrupo(grupoActualizado.getCodGrupo());
                    grupo.setNumAlumnos(grupoActualizado.getNumAlumnos());
                    grupo.setActivo(grupoActualizado.getActivo());
                    grupo.setTutorId(grupoActualizado.getTutorId());
                    return gruposRepository.save(grupo);
                })
                .orElseGet(() -> {
                    grupoActualizado.setIdGrupo(id);
                    return gruposRepository.save(grupoActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteGrupo(@PathVariable Integer id) {
        gruposRepository.deleteById(id);
    }
}

