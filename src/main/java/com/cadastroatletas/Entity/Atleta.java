package com.cadastroatletas.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "atletas")
public class Atleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String nomeCompleto;
    private String email;
    private String senha;
    private String papel;
    private String telefone;
    private LocalDate dataNascimento;
    private String graduacao; // Ex: Branca, Azul, Preta (Shodan)
    private LocalDate dataUltimaGraduacao; // Novo campo para histórico
    private String turno;
    private String nomeResponsavel;
    private Boolean ativo = true; // Fundamental para o filtro de mensagens
    private Integer diaVencimento = 10;
    private String statusPagamento;
    private String sexo;
    private LocalDate ultimaNotificacao;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pagamento> pagamentos;

    public Atleta() {}

    // --- Getters e Setters Atualizados ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getGraduacao() { return graduacao; }
    public void setGraduacao(String graduacao) {
        this.graduacao = graduacao;
        // Toda vez que setar uma nova graduação, ele pode atualizar a data automaticamente
        this.dataUltimaGraduacao = LocalDate.now();
    }

    public LocalDate getDataUltimaGraduacao() { return dataUltimaGraduacao; }
    public void setDataUltimaGraduacao(LocalDate dataUltimaGraduacao) { this.dataUltimaGraduacao = dataUltimaGraduacao; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public Boolean isAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Integer getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(Integer diaVencimento) { this.diaVencimento = diaVencimento; }

    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public List<Pagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; }

    public LocalDate getUltimaNotificacao() { return ultimaNotificacao; }
    public void setUltimaNotificacao(LocalDate ultimaNotificacao) { this.ultimaNotificacao = ultimaNotificacao; }
}