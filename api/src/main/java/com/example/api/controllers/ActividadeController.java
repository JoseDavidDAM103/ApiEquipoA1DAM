package com.example.api.controllers;

import com.example.api.models.Actividad;
import com.example.api.repositories.ActividadRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Optional;


@RestController
    @RequestMapping("/api/Actividad")
    public class ActividadeController {

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

    @GetMapping("/authorization/{id}")
    public ResponseEntity<InputStreamResource> getExcelAuthorization(@PathVariable Integer id) {

        Optional<Actividad> actividad = ActividadRepository.findById(id);


        ClassPathResource plantillaResource = new ClassPathResource("documents/plantilla_autorizacion.xlsx");

        try (
                InputStream plantillaStream = plantillaResource.getInputStream();
                Workbook workbook = new XSSFWorkbook(plantillaStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            sheet.getRow(5).getCell(4).setCellValue(actividad.get().getTitulo());
            sheet.getRow(10).getCell(9).setCellValue(actividad.get().getImportePorAlumno().toString());
            sheet.getRow(13).getCell(9).setCellValue(actividad.get().getFini().toString());
            sheet.getRow(15).getCell(9).setCellValue(actividad.get().getHini().toString());
            sheet.getRow(15).getCell(25).setCellValue(actividad.get().getHfin().toString());
            sheet.getRow(20).getCell(3).setCellValue(actividad.get().getDescripcion());
            sheet.getRow(27).getCell(3).setCellValue(actividad.get().getComentarios());

            workbook.write(bos);

            ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename("authorization.xlsx").build()
            );
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

}