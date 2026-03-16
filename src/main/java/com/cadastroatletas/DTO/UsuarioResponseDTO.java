package com.cadastroatletas.DTO;

public class UsuarioResponseDTO {
    private String nomeCompleto;
    private String papel;
    private String email;
    private String fotoUrl; // Inclua se quiser mostrar a foto no login

    public UsuarioResponseDTO(String nome, String papel, String email, String foto) {
        this.nomeCompleto = nome;
        this.papel = papel;
        this.email = email;
        this.fotoUrl = foto;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
