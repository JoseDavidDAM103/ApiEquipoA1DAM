package com.example.api.repositories;

import com.example.api.models.Actividad;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActividadRepository extends ListCrudRepository<Actividad, Integer> {


  long countAllBySolicitante_Depart_Id(Integer solicitanteDepartId);

  long countAllByTransporteReq(@NotNull Byte transporteReq);

  long countByEstado(@NotNull String estado);

  long countAllByEstado(@NotNull String estado);
}