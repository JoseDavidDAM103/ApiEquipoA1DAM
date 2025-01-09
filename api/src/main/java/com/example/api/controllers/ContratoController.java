package com.example.api.controllers;

import com.example.api.models.Contrato;
import com.example.api.repositories.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/Contrato")
public class ContratoController {

    @Autowired
    private ContratoRepository contratoRepository;

    @GetMapping
    public List<Contrato> getAllContrato() {
        return contratoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contrato getContratoById(@PathVariable Integer id) {
        Optional<Contrato> contrato = contratoRepository.findById(id);
        return contrato.orElse(null);
    }

    @PostMapping
    public Contrato createContrato(@RequestBody Contrato nuevoContrato) {
        return contratoRepository.save(nuevoContrato);
    }

    @PutMapping("/{id}")
    public Contrato updateContrato(@PathVariable Integer id, @RequestBody Contrato contratoActualizado) {
        return contratoRepository.findById(id)
                .map(contrato -> {
                    contrato.setActividadId(contratoActualizado.getActividadId());
                    contrato.setEmpTransporteId(contratoActualizado.getEmpTransporteId());
                    contrato.setContratada(contratoActualizado.getContratada());
                    contrato.setImporte(contratoActualizado.getImporte());
                    // Continúa con todos los campos
                    return contratoRepository.save(contrato);
                })
                .orElseGet(() -> {
                    contratoActualizado.setId(id);
                    return contratoRepository.save(contratoActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteContrato(@PathVariable Integer id) {
        contratoRepository.deleteById(id);
    }
}
