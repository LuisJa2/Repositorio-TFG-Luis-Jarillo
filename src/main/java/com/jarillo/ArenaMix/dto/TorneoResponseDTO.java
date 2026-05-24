package com.jarillo.ArenaMix.dto;

import java.time.LocalDateTime;

public class TorneoResponseDTO {

    private Integer id;
    private String nombre;
    private String deporte;
    private String organizadorUsername;
    private String estado;
    private LocalDateTime fechaCreacion;

    public TorneoResponseDTO() {}

    private String formato;

    public TorneoResponseDTO(Integer id, String nombre, String deporte,
                              String organizadorUsername, String estado,
                              LocalDateTime fechaCreacion, String formato) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.organizadorUsername = organizadorUsername;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.formato = formato;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; }

    public String getOrganizadorUsername() { return organizadorUsername; }
    public void setOrganizadorUsername(String organizadorUsername) {
        this.organizadorUsername = organizadorUsername;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}
