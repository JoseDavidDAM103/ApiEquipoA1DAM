package com.example.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    @Autowired
    private FotosRepository fotosRepository;

    @GetMapping
    public List<Fotos> getAllFotos() {
        return fotosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Fotos getFotoById(@PathVariable Integer id) {
        Optional<Fotos> foto = fotosRepository.findById(id);
        return foto.orElse(null);
    }

    @PostMapping
    public Fotos createFoto(@RequestBody Fotos nuevaFoto) {
        return fotosRepository.save(nuevaFoto);
    }

    @PutMapping("/{id}")
    public Fotos updateFoto(@PathVariable Integer id, @RequestBody Fotos fotoActualizada) {
        return fotosRepository.findById(id)
                .map(foto -> {
                    foto.setUrlFoto(fotoActualizada.getUrlFoto());
                    foto.setDescripcion(fotoActualizada.getDescripcion());
                    foto.setActividadId(fotoActualizada.getActividadId());
                    return fotosRepository.save(foto);
                })
                .orElseGet(() -> {
                    fotoActualizada.setId(id);
                    return fotosRepository.save(fotoActualizada);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteFoto(@PathVariable Integer id) {
        fotosRepository.deleteById(id);
    }
}

