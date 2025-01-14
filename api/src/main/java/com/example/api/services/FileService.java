package com.example.api.services;

import com.example.api.models.Actividad;
import com.example.api.models.Contrato;
import com.example.api.models.Foto;
import com.example.api.models.Profesor;
import com.example.api.repositories.ActividadRepository;
import com.example.api.repositories.ContratoRepository;
import com.example.api.repositories.FotoRepository;
import com.example.api.repositories.ProfesorRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class FileService {

    public ContratoRepository contratoRepository;
    public ActividadRepository actividadRepository;
    public ProfesorRepository profesorRepository;
    public FotoRepository fotoRepository;

    public final String URL_PRESUPUESTO = "/documentos/presupuestos/";
    public final String URL_FACTURA = "/documentos/facturas/";
    public final String URL_FOLLETOS = "/documentos/folletos/";
    public final String URL_FOTOS = "/imagenes/actividad/fotos/";
    public final String URL_FOTOS_PROF = "/imagenes/profesores/";

    public ResponseEntity<String> saveArchivo(@RequestParam("id") int id,
                                              @RequestParam("fichero") MultipartFile multipartFile,
                                              @RequestParam("tipo") String tipo) {
        // Verificación de que el archivo no está vacío
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No se ha seleccionado un archivo.");
        }

        String nombreArchivo = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDirectory = "";

        File directory;
        switch (tipo.toLowerCase()) {
            case "presupuesto" -> {
                Contrato contrato = contratoRepository.findById(id).orElse(null);

                uploadDirectory = URL_PRESUPUESTO + contrato.getActividad().getTitulo();

                directory = new File(uploadDirectory);

                if (!directory.exists()) {
                    directory.mkdirs();
                }


                String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

                // Validar si el archivo es una imagen o un PDF
                boolean esPDF = extension.equals("pdf");


                if (esPDF) {
                    if (contrato != null) {
                        contrato.setUrlPresupuesto(nombreArchivo);
                    }

                    contratoRepository.save(contrato);

                    return ResponseEntity.ok("Archivo subido correctamente para el presupuesto para el contrato de la actividad " + contrato.getActividad().getTitulo());
                }
                break;
            }

            case "foto" -> {

                uploadDirectory = URL_FOTOS;

                directory = new File(uploadDirectory);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Foto foto = fotoRepository.findById(id).orElse(null);

                String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

                // Validar si el archivo es una imagen o un PDF
                boolean esImagen = extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");


                if (esImagen) {
                    if (foto != null) {
                        foto.setUrlFoto(nombreArchivo);
                    }

                    fotoRepository.save(foto);

                    return ResponseEntity.ok("Foto subida correctamente de la actividad: " + foto.getActividad().getTitulo());
                }
            }
            case "factura" -> {
                uploadDirectory = URL_FACTURA;

                directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Contrato contrato = contratoRepository.findById(id).orElse(null);

                String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

                // Validar si el archivo es una imagen o un PDF
                boolean esPDF = extension.equals("pdf");


                if (esPDF) {
                    if (contrato != null) {
                        contrato.setUrlFactura(nombreArchivo);
                    }

                    contratoRepository.save(contrato);

                    return ResponseEntity.ok("Archivo subido correctamente la factura para el contrato de la actividad " + contrato.getActividad().getTitulo());

                }
                break;
            }

            case "folleto" -> {
                uploadDirectory = URL_FOLLETOS;

                directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Actividad actividad = actividadRepository.findById(id).orElse(null);

                String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

                // Validar si el archivo es una imagen o un PDF
                boolean esPDF = extension.equals("pdf");


                if (esPDF) {
                    if (actividad != null) {
                        actividad.setUrlFolleto(nombreArchivo);
                    }

                    actividadRepository.save(actividad);

                    return ResponseEntity.ok("Archivo subido correctamente el folleto para la actividad " + actividad.getTitulo());

                }
                break;
            }
        }
        return ResponseEntity.badRequest().body("Error al subir el archivo");

    }

    public ResponseEntity<String> saveFotoProfesor(MultipartFile multipartFile,
                                                   String correo) {
        // Verificación de que el archivo no está vacío
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No se ha seleccionado un archivo.");
        }

        String nombreArchivo = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDirectory = "";

        File directory;

        uploadDirectory = URL_FOTOS_PROF;

        directory = new File(uploadDirectory);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Profesor profesor = profesorRepository.findProfesorsByCorreo(correo).orElse(null);

        String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

        // Validar si el archivo es una imagen o un PDF
        boolean esImagen = extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");


        if (esImagen) {
            if (profesor != null) {
                profesor.setUrlFoto(nombreArchivo);
            }

            profesorRepository.save(profesor);

            return ResponseEntity.ok("Foto subida correctamente del profesor " + profesor.getNombre());
        }
        return ResponseEntity.badRequest().body("Error al subir el archivo");
    }


    public ResponseEntity<Resource> getArchivoPDF(@RequestParam("id") int id,
                                                  @RequestParam(value = "tipo", required = false) String tipo) {

        String nombreArchivo = null;
        switch (tipo.toLowerCase()) {
            case "factura" -> {
                Contrato contrato = contratoRepository.findById(id).orElse(null);

                if (contrato != null) {
                    nombreArchivo = contrato.getUrlFactura();

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }
            case "presupuesto" -> {
                Contrato contrato = contratoRepository.findById(id).orElse(null);
                if (contrato != null) {
                    nombreArchivo = contrato.getUrlPresupuesto();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }
            case "folleto" -> {
                Actividad actividad = actividadRepository.findById(id).orElse(null);
                if (actividad != null) {
                    nombreArchivo = actividad.getUrlFolleto();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }

        }
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        try {
            // Ruta al archivo almacenado
            Path filePath = Paths.get(tipo.equalsIgnoreCase("folleto") ? URL_FOLLETOS : tipo.equalsIgnoreCase("factura") ? URL_FACTURA : URL_PRESUPUESTO).resolve(nombreArchivo);
            Resource resource = new UrlResource(filePath.toUri());

            // Verificar si el archivo existe y es legible
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Resource> getArchivoFotoprofesor(@RequestParam("correo") String correo,
                                                           @RequestParam(value = "tipo", required = false) String tipo) {

        String nombreArchivo = null;
        Profesor profesor = profesorRepository.findProfesorsByCorreo(correo).orElse(null);

        if (profesor != null) {
            nombreArchivo = profesor.getUrlFoto();
        }
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        try {
            // Ruta al archivo almacenado
            Path filePath = Paths.get(URL_FOTOS_PROF).resolve(nombreArchivo);
            Resource resource = new UrlResource(filePath.toUri());

            // Verificar si el archivo existe y es legible
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<Resource>> getArchivoFotosActividad(@RequestParam("id") int id,
                                                                   @RequestParam(value = "tipo", required = false) String tipo) {
        List<Resource> fotos = new ArrayList<>();
        String nombreArchivo = null;
        Actividad actividad = actividadRepository.findById(id).orElse(null);
        if (actividad != null) {
            List<Foto> allfotos = fotoRepository.findAllByActividad(actividad);
            for (Foto foto : allfotos) {
                nombreArchivo = foto.getUrlFoto();
                try {
                    // Ruta al archivo almacenado
                    Path filePath = Paths.get(URL_FOTOS).resolve(nombreArchivo);
                    Resource resource = new UrlResource(filePath.toUri());

                    // Verificar si el archivo existe y es legible
                    if (!resource.exists() || !resource.isReadable()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                    }

                    fotos.add(resource);

                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }

            }
        }


        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String texto = "";
        for (Resource foto : fotos) {
            texto += "attachment; filename=\"" + foto.getFilename() + "\"";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, texto)
                .body(fotos);


    }


}


