package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursosRepository cursosRepository;

    @GetMapping
    public List<Cursos> getAllCursos() {
        return cursosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Cursos getCursoById(@PathVariable Integer id) {
        Optional<Cursos> curso = cursosRepository.findById(id);
        return curso.orElse(null);
    }

    @PostMapping
    public Cursos createCurso(@RequestBody Cursos nuevoCurso) {
        return cursosRepository.save(nuevoCurso);
    }

    @PutMapping("/{id}")
    public Cursos updateCurso(@PathVariable Integer id, @RequestBody Cursos cursoActualizado) {
        return cursosRepository.findById(id)
                .map(curso -> {
                    curso.setCodCurso(cursoActualizado.getCodCurso());
                    curso.setTitulo(cursoActualizado.getTitulo());
                    curso.setEtapa(cursoActualizado.getEtapa());
                    curso.setNivel(cursoActualizado.getNivel());
                    curso.setActivo(cursoActualizado.getActivo());
                    return cursosRepository.save(curso);
                })
                .orElseGet(() -> {
                    cursoActualizado.setIdCurso(id);
                    return cursosRepository.save(cursoActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteCurso(@PathVariable Integer id) {
        cursosRepository.deleteById(id);
    }
}

