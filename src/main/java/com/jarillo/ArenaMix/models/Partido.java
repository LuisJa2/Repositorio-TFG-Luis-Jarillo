package com.jarillo.ArenaMix.models;

import jakarta.persistence.*;

@Entity
@Table(name = "partidos")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    @Column(name = "nombre_local", length = 100)
    private String nombreLocal;

    @Column(name = "nombre_visitante", length = 100)
    private String nombreVisitante;

    @ManyToOne
    @JoinColumn(name = "local_id", nullable = true)
    private Usuario local;

    @ManyToOne
    @JoinColumn(name = "visitante_id", nullable = true)
    private Usuario visitante;

    @Column(name = "puntos_local")
    private Integer puntosLocal = 0;

    @Column(name = "puntos_visitante")
    private Integer puntosVisitante = 0;

    @Column(nullable = false)
    private Integer ronda;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    public Partido() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }

    public String getNombreLocal() { return nombreLocal; }
    public void setNombreLocal(String nombreLocal) { this.nombreLocal = nombreLocal; }

    public String getNombreVisitante() { return nombreVisitante; }
    public void setNombreVisitante(String nombreVisitante) { this.nombreVisitante = nombreVisitante; }

    public Usuario getLocal() { return local; }
    public void setLocal(Usuario local) { this.local = local; }

    public Usuario getVisitante() { return visitante; }
    public void setVisitante(Usuario visitante) { this.visitante = visitante; }

    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }

    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }

    public Integer getRonda() { return ronda; }
    public void setRonda(Integer ronda) { this.ronda = ronda; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}