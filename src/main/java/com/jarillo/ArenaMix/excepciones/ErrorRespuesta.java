package com.jarillo.ArenaMix.excepciones;

import java.time.LocalDateTime;

public class ErrorRespuesta {

    private int codigo;
    private String error;
    private String mensaje;
    private LocalDateTime timestamp;

    public ErrorRespuesta() {}

    public ErrorRespuesta(int codigo, String error, String mensaje, LocalDateTime timestamp) {
        this.codigo = codigo;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
