package com.cadastroatletas.DTO;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String nomeCompleto;
    private String email;
    private String papel;
    private String graduacao;
    private String numeroZempo;
    private String senha; // ADICIONADO PARA RESOLVER O ERRO NO CONTROLLER
    private String fotoUrl;
    private int diaVencimento;

    // --- GETTERS E SETTERS ---

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getGraduacao() {
        return graduacao;
    }

    public void setGraduacao(String graduacao) {
        this.graduacao = graduacao;
    }

    public String getNumeroZempo() {
        return numeroZempo;
    }

    public void setNumeroZempo(String numeroZempo) {
        this.numeroZempo = numeroZempo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(int diaVencimento) {
        this.diaVencimento = diaVencimento;
    }
}