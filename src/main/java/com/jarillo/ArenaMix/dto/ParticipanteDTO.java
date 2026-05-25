package com.jarillo.ArenaMix.dto;

public class ParticipanteDTO {
    private Integer id;
    private String nombre;
    private Integer torneoId;

    public ParticipanteDTO() {}

    public ParticipanteDTO(Integer id, String nombre, Integer torneoId) {
        this.id = id;
        this.nombre = nombre;
        this.torneoId = torneoId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getTorneoId() { return torneoId; }
    public void setTorneoId(Integer torneoId) { this.torneoId = torneoId; }
}
