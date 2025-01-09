package com.example.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "prof_participantes")
public class ProfParticipante {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividade actividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesore profesor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Actividade getActividad() {
        return actividad;
    }

    public void setActividad(Actividade actividad) {
        this.actividad = actividad;
    }

    public Profesore getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesore profesor) {
        this.profesor = profesor;
    }

}