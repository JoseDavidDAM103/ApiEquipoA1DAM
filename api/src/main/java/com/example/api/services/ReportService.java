package com.example.api.services;

import com.example.api.models.Departamento;
import com.example.api.models.Grupo;
import com.example.api.repositories.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@Service

public class ReportService {
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private GrupoRepository GrupoRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private GrupoParticipanteRepository grupoParticipanteRepository;


   /* public long totalpormes(short mes){
        int year=LocalDate.now().getYear();
        List<Actividad> actividades = actividadRepository.findAllByFiniBetween(LocalDate.of(year, mes, 1), LocalDate.of(LocalDate.now().getYear(), mes, YearMonth.of(year,mes).lengthOfMonth()));
        for (Actividad actividad : actividades) {
            if (actividad.getEstado().equalsIgnoreCase("Realizada")) {
            }else{
                actividades.remove(actividad);
            }
        }
        return actividades.size();
    }*/
   public long totalActividades(){
       return actividadRepository.countAllByEstado("REALIZADA");
   }
    public Map<String,Long> totalpordepartamento(){
        Map<String,Long> totalpordepartamento = new HashMap<>();
        List<Departamento> departamentos = departamentoRepository.findAll();
        for (Departamento departamento : departamentos) {
            totalpordepartamento.put(departamento.getCodigo(),actividadRepository.countAllBySolicitante_Depart_Id(departamento.getId()));
        }
        return totalpordepartamento;
    }
    public long totalprevista(){
        return actividadRepository.countByEstado(("APROBADA"));
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
                totalporGrupo.put(grupo.getCurso().getCodCurso(),totalporGrupo.get(grupo.getCurso().getCodCurso())+grupoParticipanteRepository.countAllByGrupo(grupo) );
        }
        return totalporGrupo;
    }
    public ResponseEntity<InputStreamResource> getinformes() {

        Map<String,Long> totalporcurso=totalporCurso();
        Map<String,Long> totalporGrupo=totalporGrupo();
        Map<String,Long> totalpordepartamento=totalpordepartamento();
        long totalprevista=totalprevista();
        long total=totalActividades();
       AtomicInteger count= new AtomicInteger(1);

        ClassPathResource plantillaResource = new ClassPathResource("documents/plantilla_autorizacion.xlsx");

        try (
                InputStream plantillaStream = plantillaResource.getInputStream();
                Workbook workbook = new XSSFWorkbook(plantillaStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            totalporcurso.forEach((k,c)->{sheet.getRow(count.get()).getCell(1).setCellValue(k); sheet.getRow(count.getAndIncrement()).getCell(2).setCellValue(c);}  );
            count.set(1);
            totalporGrupo.forEach((k,c)->{sheet.getRow(count.get()).getCell(3).setCellValue(k); sheet.getRow(count.getAndIncrement()).getCell(4).setCellValue(c);});
            count.set(1);
            sheet.getRow(count.get()).getCell(5).setCellValue("Actividades Totales");
            sheet.getRow(count.getAndIncrement()).getCell(6).setCellValue(total);
            sheet.getRow(count.get()).getCell(7).setCellValue("Total Previstas");
            sheet.getRow(count.getAndIncrement()).getCell(8).setCellValue(totalprevista);
            count.set(1);
            totalpordepartamento.forEach((k,c)->{sheet.getRow(count.get()).getCell(9).setCellValue(k); sheet.getRow(count.getAndIncrement()).getCell(10).setCellValue(c);});


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

    public long totalcontransporte(){
        return actividadRepository.countAllByTransporteReq(((byte)1));
    }

}
