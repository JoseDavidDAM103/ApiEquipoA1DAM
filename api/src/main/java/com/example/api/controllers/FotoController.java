package com.example.api.controllers;

import com.example.api.models.Actividad;
import com.example.api.models.Foto;
import com.example.api.repositories.ActividadRepository;
import com.example.api.repositories.FotoRepository;
import com.example.api.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/foto")
public class FotoController {

    public final String URL_FOTOS = "/imagenes/actividad/";
    private FileService fileService=new FileService();
    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private ActividadRepository actividadRepository;

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

    @GetMapping("/poractividad")
    public ResponseEntity<List<org.springframework.core.io.Resource>> getFotosResources(@RequestParam("id") int id) {
        List<org.springframework.core.io.Resource> fotos= fileService.getArchivoFotosActividad(id);
        if (fotos==null){
            return ResponseEntity.notFound().build();
        }
        String texto = "";
        for (org.springframework.core.io.Resource foto : fotos) {
            texto += "attachment; filename=\"" + foto.getFilename() + "\"";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, texto)
                .body(fotos);


    }
    @PostMapping
    public ResponseEntity uploadFiles(@RequestParam("fotos") MultipartFile[] files,
                                      @RequestParam("idActividad") int idActividad,
                                      @RequestParam("descripcion") String descripcion) {

        for (MultipartFile file : files) {
            try {
                Actividad actividad = actividadRepository.findById(idActividad).get();
                String uploadDir = URL_FOTOS + actividad.getTitulo();

                // Guardar el archivo en la carpeta especificada
                File dest = new File(uploadDir + File.separator + file.getOriginalFilename());
                file.transferTo(dest);

                Foto foto = new Foto();
                foto.setUrlFoto(uploadDir + File.separator + file.getOriginalFilename());

                if(descripcion != null) {foto.setDescripcion(descripcion);}
                else {foto.setDescripcion(actividad.getTitulo());}

                foto.setActividad(actividad);
                fotoRepository.save(foto);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al subir el archivo: " + file.getOriginalFilename());
            }
        }
        return ResponseEntity.ok("Fotos subidas con éxito");
    }

}


