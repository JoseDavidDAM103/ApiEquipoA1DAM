package com.example.api.controllers;

import com.example.api.models.Contrato;
import com.example.api.repositories.ContratoRepository;
import com.example.api.services.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrato")
public class ContratoController {

    @Autowired
    private ContratoRepository contratoRepository;
    FileService fileService;

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
    public Contrato createContrato(@RequestBody Contrato nuevoContrato, @RequestBody MultipartFile presupuesto,
                                   @RequestBody MultipartFile factura) {
        boolean guardado = false;

        Contrato contrato = contratoRepository.save(nuevoContrato);

        if(contrato != null) {
            if(presupuesto.isEmpty()) {
                guardado = true;
            } else {

                String nombreArchivo = StringUtils.cleanPath(Objects.requireNonNull(presupuesto.getOriginalFilename()));
                String uploadDirectory = "C:/documents/presupuestos/" + contrato.getActividad().getId();

                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    File parentDirectory = directory.getParentFile();
                    if (!parentDirectory.exists()) {
                        parentDirectory.mkdirs();
                        directory.mkdirs();
                    }
                }
                String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

                // Validar si el archivo es una imagen o un PDF
                boolean esPDF = extension.equals("pdf");
                if (esPDF) {
                    if (contrato != null) {
                        try {
                            presupuesto.transferTo(directory);
                            contrato.setUrlPresupuesto(nombreArchivo);
                            System.out.println("Presupuesto guardado");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return contratoRepository.save(contrato);
                    }
                }
            }
            if(factura.isEmpty()) {
                guardado = true;
            }else {
                String nombreArchivo = StringUtils.cleanPath(Objects.requireNonNull(presupuesto.getOriginalFilename()));
                String uploadDirectory = "C:/documents/facturas/" + contrato.getActividad().getId();

                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    File parentDirectory = directory.getParentFile();
                    if (!parentDirectory.exists()) {
                        parentDirectory.mkdirs();
                        directory.mkdirs();
                    }
                }
                String extension = FilenameUtils.getExtension(nombreArchivo).toLowerCase();

                // Validar si el archivo es una imagen o un PDF
                boolean esPDF = extension.equals("pdf");
                if (esPDF) {
                    if (contrato != null) {
                        try {
                            presupuesto.transferTo(directory);
                            contrato.setUrlFactura(nombreArchivo);
                            System.out.println("Factura guardada");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return contratoRepository.save(contrato);
                    }
                }
            }
        }
        if(guardado) {
            contrato = contratoRepository.findById(contrato.getId()).get();
        return contrato;
        }

        contratoRepository.save(contrato);
        return null;
    }

    @PutMapping("/{id}")
    public Contrato updateContrato(@PathVariable Integer id, @RequestBody Contrato contratoActualizado,  @RequestBody MultipartFile presupuesto,
                                   @RequestBody MultipartFile factura) {
        return contratoRepository.findById(id)
                .map(contrato -> {
                    contrato.setActividad(contratoActualizado.getActividad());
                    contrato.setEmpTransporte(contratoActualizado.getEmpTransporte());
                    contrato.setContratada(contratoActualizado.getContratada());
                    contrato.setImporte(contratoActualizado.getImporte());
                    if(!presupuesto.isEmpty()) {
                        boolean guardado = fileService.saveArchivo(contrato.getId(), presupuesto,"presupuesto");
                    }
                    if(!factura.isEmpty()) {
                        boolean guardado = fileService.saveArchivo(contrato.getId(), factura,"factura");
                    }
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
