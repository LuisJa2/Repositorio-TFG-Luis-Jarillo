package com.jarillo.ArenaMix.dto;

public class RegistroRequestDTO {

    private String username;
    private String email;
    private String password;
    private String rol;

    public RegistroRequestDTO() {}

    public RegistroRequestDTO(String username, String email, String password, String rol) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
