package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentosRepository departamentosRepository;

    @GetMapping
    public List<Departamentos> getAllDepartamentos() {
        return departamentosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Departamentos getDepartamentoById(@PathVariable Integer id) {
        Optional<Departamentos> departamento = departamentosRepository.findById(id);
        return departamento.orElse(null);
    }

    @PostMapping
    public Departamentos createDepartamento(@RequestBody Departamentos nuevoDepartamento) {
        return departamentosRepository.save(nuevoDepartamento);
    }

    @PutMapping("/{id}")
    public Departamentos updateDepartamento(@PathVariable Integer id, @RequestBody Departamentos departamentoActualizado) {
        return departamentosRepository.findById(id)
                .map(departamento -> {
                    departamento.setCodigo(departamentoActualizado.getCodigo());
                    departamento.setNombre(departamentoActualizado.getNombre());
                    return departamentosRepository.save(departamento);
                })
                .orElseGet(() -> {
                    departamentoActualizado.setIdDepar(id);
                    return departamentosRepository.save(departamentoActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteDepartamento(@PathVariable Integer id) {
        departamentosRepository.deleteById(id);
    }
}

