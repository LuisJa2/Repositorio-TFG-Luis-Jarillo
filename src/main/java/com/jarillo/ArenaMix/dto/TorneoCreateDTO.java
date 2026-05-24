package com.jarillo.ArenaMix.dto;

public class TorneoCreateDTO {

    private String nombre;
    private String deporte;
    private Integer organizadorId;

    public TorneoCreateDTO() {}

    private String formato;

    public TorneoCreateDTO(String nombre, String deporte, Integer organizadorId, String formato) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.organizadorId = organizadorId;
        this.formato = formato;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; }

    public Integer getOrganizadorId() { return organizadorId; }
    public void setOrganizadorId(Integer organizadorId) { this.organizadorId = organizadorId; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}
