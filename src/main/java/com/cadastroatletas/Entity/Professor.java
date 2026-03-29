package com.cadastroatletas.Entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "professores")
public class Professor implements UserDetails { // <-- Implementação para o Security

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroZempo;

    private String nomeCompleto;
    private String graduacao;
    private Integer idade;
    private String sexo;
    private String numeroContato;
    private String email;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "senha", length = 100) // Espaço para o Hash do BCrypt
    private String senha;

    private String papel; // Ex: "ADMIN", "INSTRUTOR"

    // --- MÉTODOS OBRIGATÓRIOS DO USERDETAILS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Se o papel for "ADMIN", ele ganha essa autoridade
        return List.of(new SimpleGrantedAuthority("ROLE_" + (papel != null ? papel.toUpperCase() : "PROFESSOR")));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email; // O e-mail continua sendo o login
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // --- SEUS GETTERS E SETTERS ---
    // (Pode manter todos os que você já tem abaixo)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroZempo() { return numeroZempo; }
    public void setNumeroZempo(String numeroZempo) { this.numeroZempo = numeroZempo; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getGraduacao() { return graduacao; }
    public void setGraduacao(String graduacao) { this.graduacao = graduacao; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getNumeroContato() { return numeroContato; }
    public void setNumeroContato(String numeroContato) { this.numeroContato = numeroContato; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
}