package com.example.api.controllers;

import com.example.api.models.Actividade;
import com.example.api.repositories.ActividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
    @RequestMapping("/api/Actividade")
    public class ActividadeController {

        @Autowired
        private ActividadeRepository ActividadeRepository;

        // GET: Listar todas
        @GetMapping
        public List<Actividade> getAllActividade() {
            return ActividadeRepository.findAll();
        }

        // GET: Obtener por ID
        @GetMapping("/{id}")
        public Actividade getActividadById(@PathVariable Integer id) {
            Optional<Actividade> actividad = ActividadeRepository.findById(id);
            return actividad.orElse(null);
        }

        // POST: Crear nueva
        @PostMapping
        public Actividade createActividad(@RequestBody Actividade nuevaActividad) {
            return ActividadeRepository.save(nuevaActividad);
        }

        // PUT: Actualizar existente
        @PutMapping("/{id}")
        public Actividade updateActividad(@PathVariable Integer id, @RequestBody Actividade actividadActualizada) {
            return ActividadeRepository.findById(id)
                    .map(actividad -> {
                        actividad.setTitulo(actividadActualizada.getTitulo());
                        actividad.setTipo(actividadActualizada.getTipo());
                        actividad.setDescripcion(actividadActualizada.getDescripcion());
                        actividad.setFini(actividadActualizada.getFini());
                        actividad.setFfin(actividadActualizada.getFfin());
                        actividad.setHini(actividadActualizada.getHini());
                        actividad.setHfin(actividadActualizada.getHfin());
                        // Continúa con todos los campos que necesites actualizar
                        return ActividadeRepository.save(actividad);
                    })
                    .orElseGet(() -> {
                        // Si no existe, se crea uno nuevo con el ID especificado
                        actividadActualizada.setId(id);
                        return ActividadeRepository.save(actividadActualizada);
                    });
        }

        // DELETE: Eliminar
        @DeleteMapping("/{id}")
        public void deleteActividad(@PathVariable Integer id) {
            ActividadeRepository.deleteById(id);
        }


    }