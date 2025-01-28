package com.example.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "localizaciones")
public class Localizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_Actividad", nullable = false)
    private Actividad idActividad;

    @NotNull
    @Column(name = "latitud", nullable = false)
    private Integer latitud;

    @NotNull
    @Column(name = "longitud", nullable = false)
    private Integer longitud;

    @Size(max = 45)
    @Column(name = "comentario", length = 45)
    private String comentario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Actividad getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Actividad idActividad) {
        this.idActividad = idActividad;
    }

    public Integer getLatitud() {
        return latitud;
    }

    public void setLatitud(Integer latitud) {
        this.latitud = latitud;
    }

    public Integer getLongitud() {
        return longitud;
    }

    public void setLongitud(Integer longitud) {
        this.longitud = longitud;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}