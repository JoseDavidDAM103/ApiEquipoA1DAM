package com.example.api.controllers;

import com.example.api.models.Foto;
import com.example.api.repositories.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/foto")
public class FotoController {

    @Autowired
    private FotoRepository fotoRepository;

    @GetMapping
    public List<Foto> getAllFoto() {
        return fotoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Foto getFotoById(@PathVariable Integer id) {
        Optional<Foto> foto = fotoRepository.findById(id);
        return foto.orElse(null);
    }

    @PostMapping
    public Foto createFoto(@RequestBody Foto nuevaFoto) {
        return fotoRepository.save(nuevaFoto);
    }

    @PutMapping("/{id}")
    public Foto updateFoto(@PathVariable Integer id, @RequestBody Foto fotoActualizada) {
        return fotoRepository.findById(id)
                .map(foto -> {
                    foto.setUrlFoto(fotoActualizada.getUrlFoto());
                    foto.setDescripcion(fotoActualizada.getDescripcion());
                    foto.setActividad(fotoActualizada.getActividad());
                    return fotoRepository.save(foto);
                })
                .orElseGet(() -> {
                    fotoActualizada.setId(id);
                    return fotoRepository.save(fotoActualizada);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteFoto(@PathVariable Integer id) {
        fotoRepository.deleteById(id);
    }
}

