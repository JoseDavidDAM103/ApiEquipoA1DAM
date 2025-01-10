package com.example.api.controllers;

import com.example.api.models.Actividad;
import com.example.api.repositories.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
    @RequestMapping("/api/Actividad")
    public class ActividadController {

        @Autowired
        private ActividadRepository ActividadRepository;

        @GetMapping
        public List<Actividad> getAllActividades() {
            return ActividadRepository.findAll();
        }

        @GetMapping("/{id}")
        public Actividad getActividadById(@PathVariable Integer id) {
            Optional<Actividad> actividad = ActividadRepository.findById(id);
            return actividad.orElse(null);
        }

        @PostMapping
        public Actividad createActividad(@RequestBody Actividad nuevaActividad) {
            return ActividadRepository.save(nuevaActividad);
        }

        @PutMapping("/{id}")
        public Actividad updateActividad(@PathVariable Integer id, @RequestBody Actividad actividadActualizada) {
            return ActividadRepository.findById(id)
                    .map(actividad -> {
                        actividad.setTitulo(actividadActualizada.getTitulo());
                        actividad.setTipo(actividadActualizada.getTipo());
                        actividad.setDescripcion(actividadActualizada.getDescripcion());
                        actividad.setFini(actividadActualizada.getFini());
                        actividad.setFfin(actividadActualizada.getFfin());
                        actividad.setHini(actividadActualizada.getHini());
                        actividad.setHfin(actividadActualizada.getHfin());
                        return ActividadRepository.save(actividad);
                    })
                    .orElseGet(() -> {
                        actividadActualizada.setId(id);
                        return ActividadRepository.save(actividadActualizada);
                    });
        }

        @DeleteMapping("/{id}")
        public void deleteActividad(@PathVariable Integer id) {
            ActividadRepository.deleteById(id);
        }


    }