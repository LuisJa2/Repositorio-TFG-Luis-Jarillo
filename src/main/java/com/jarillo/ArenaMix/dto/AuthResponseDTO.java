package com.jarillo.ArenaMix.dto;

public class AuthResponseDTO {

    private String token;
    private String username;
    private String rol;
    private Integer usuarioId;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String username, String rol, Integer usuarioId) {
        this.token = token;
        this.username = username;
        this.rol = rol;
        this.usuarioId = usuarioId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}
