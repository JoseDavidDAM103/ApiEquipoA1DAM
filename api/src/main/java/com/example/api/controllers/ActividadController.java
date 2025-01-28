package com.example.api.controllers;


import com.example.api.models.Actividad;
import com.example.api.models.Departamento;
import com.example.api.models.Grupo;
import com.example.api.repositories.ActividadRepository;
import com.example.api.repositories.DepartamentoRepository;
import com.example.api.repositories.GrupoParticipanteRepository;
import com.example.api.repositories.GrupoRepository;
import com.example.api.services.FileService;
import com.example.api.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/api/actividad")
public class ActividadController {

    @Autowired
    private ActividadRepository ActividadRepository;
    @Autowired
    private com.example.api.repositories.GrupoRepository GrupoRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private GrupoParticipanteRepository grupoParticipanteRepository;
    private FileService fileservice = new FileService();
    private ReportService reportservice = new ReportService();

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

    public long totalActividades(){
        return ActividadRepository.countAllByEstado("REALIZADA");
    }
    public Map<String,Long> totalpordepartamento(){
        Map<String,Long> totalpordepartamento = new HashMap<>();
        List<Departamento> departamentos = departamentoRepository.findAll();
        for (Departamento departamento : departamentos) {
            totalpordepartamento.put(departamento.getCodigo(),ActividadRepository.countAllBySolicitante_Depart_Id(departamento.getId()));
        }
        return totalpordepartamento;
    }
    public long totalprevista(){
        return ActividadRepository.countByEstado(("APROBADA"));
    }
    public Map<String,Long> totalporGrupo(){
        Map<String,Long> totalporGrupo = new HashMap<>();
        List<Grupo> grupos = GrupoRepository.findAll();
        for (Grupo grupo : grupos) {

            totalporGrupo.put(grupo.getCodGrupo(),grupoParticipanteRepository.countAllByGrupo(grupo));
        }
        return totalporGrupo;
    }
    public Map<String,Long> totalporCurso(){
        Map<String,Long> totalporGrupo = new HashMap<>();
        List<Grupo> grupos = GrupoRepository.findAll();
        for (Grupo grupo : grupos) {
            if (totalporGrupo.containsKey(grupo.getCurso().getCodCurso())) {
                totalporGrupo.put(grupo.getCurso().getCodCurso(),totalporGrupo.get(grupo.getCurso().getCodCurso())+grupoParticipanteRepository.countAllByGrupo(grupo) );
            }else {
                totalporGrupo.put(grupo.getCurso().getCodCurso(),grupoParticipanteRepository.countAllByGrupo(grupo) );
            }

        }
        return totalporGrupo;
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
                    actividad.setPrevistaIni(actividadActualizada.getPrevistaIni());
                    actividad.setTransporteReq(actividadActualizada.getTransporteReq());
                    actividad.setComentTransporte(actividadActualizada.getComentTransporte());
                    actividad.setAlojamientoReq(actividadActualizada.getAlojamientoReq());
                    actividad.setComentAlojamiento(actividadActualizada.getComentAlojamiento());
                    actividad.setComentarios(actividadActualizada.getComentarios());
                    actividad.setEstado(actividadActualizada.getEstado());
                    actividad.setComentEstado(actividadActualizada.getComentEstado());
                    actividad.setIncidencias(actividadActualizada.getIncidencias());
                    actividad.setImportePorAlumno(actividadActualizada.getImportePorAlumno());

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
    @GetMapping("/reports")
    public ResponseEntity<InputStreamResource> getinformes() {

        Map<String,Long> totalporcurso=totalporCurso();
        Map<String,Long> totalporGrupo=totalporGrupo();
        Map<String,Long> totalpordepartamento=totalpordepartamento();
        long totalprevista=totalprevista();
        long total=totalActividades();
        AtomicInteger count= new AtomicInteger(0);

        ClassPathResource plantillaResource = new ClassPathResource("documents/Libro1.xlsx");

        try (
                InputStream plantillaStream = plantillaResource.getInputStream();
                Workbook workbook = new XSSFWorkbook(plantillaStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            totalporcurso.forEach((k,c)->{if (sheet.getRow(count.get())==null){sheet.createRow(count.get());}  sheet.getRow(count.get()).createCell(0).setCellValue(k); sheet.getRow(count.getAndIncrement()).createCell(1).setCellValue(c);}  );
            count.set(0);
            totalporGrupo.forEach((k,c)->{if (sheet.getRow(count.get())==null){sheet.createRow(count.get());} sheet.getRow(count.get()).createCell(2).setCellValue(k); sheet.getRow(count.getAndIncrement()).createCell(3).setCellValue(c);});
            count.set(0);
            sheet.getRow(count.get()).createCell(4).setCellValue("Actividades Totales");
            sheet.getRow(count.get()).createCell(5).setCellValue(total);
            sheet.getRow(count.get()+1).createCell(4).setCellValue("Total Previstas");
            sheet.getRow(count.get()+1).createCell(5).setCellValue(totalprevista);
            count.set(0);
            totalpordepartamento.forEach((k,c)->{if (sheet.getRow(count.get())==null){sheet.createRow(count.get());} sheet.getRow(count.get()).createCell(8).setCellValue(k); sheet.getRow(count.getAndIncrement()).createCell(9).setCellValue(c);});


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


    @GetMapping("/documentos")
    public ResponseEntity<Resource> getArchivoPDF(@RequestParam("id") int id, @RequestParam(value = "tipo", required = false) String tipo) {
      
        Resource resource = fileservice.getArchivoPDF(id, tipo);
      
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        String texto = "";

        texto += "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, texto)
                .body(resource);

    }
}