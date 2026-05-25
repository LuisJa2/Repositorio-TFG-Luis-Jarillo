package com.jarillo.ArenaMix.dto;

public class PartidoDTO {
    private Integer id;
    private String nombreLocal;
    private String nombreVisitante;
    private Integer puntosLocal;
    private Integer puntosVisitante;
    private Integer ronda;
    private String estado;

    public PartidoDTO() {}

    public PartidoDTO(Integer id, String nombreLocal, String nombreVisitante,
                      Integer puntosLocal, Integer puntosVisitante, Integer ronda, String estado) {
        this.id = id;
        this.nombreLocal = nombreLocal;
        this.nombreVisitante = nombreVisitante;
        this.puntosLocal = puntosLocal;
        this.puntosVisitante = puntosVisitante;
        this.ronda = ronda;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombreLocal() { return nombreLocal; }
    public void setNombreLocal(String nombreLocal) { this.nombreLocal = nombreLocal; }

    public String getNombreVisitante() { return nombreVisitante; }
    public void setNombreVisitante(String nombreVisitante) { this.nombreVisitante = nombreVisitante; }

    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }

    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }

    public Integer getRonda() { return ronda; }
    public void setRonda(Integer ronda) { this.ronda = ronda; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
