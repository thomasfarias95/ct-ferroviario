package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {

    // 1. Para sua lista de presença de Abril:
    List<Atleta> findByTurnoAndAtivoTrue(String turno);

    // 2. Para o seu Gráfico de Pizza (Contagem por Sexo):
    @Query("SELECT a.sexo, COUNT(a) FROM Atleta a WHERE a.ativo = true GROUP BY a.sexo")
    List<Object[]> countAtletasBySexo();

    // 3. Para o seu Gráfico de Idades (Agrupado por ano de nascimento):
    @Query("SELECT YEAR(a.dataNascimento), COUNT(a) FROM Atleta a GROUP BY YEAR(a.dataNascimento)")
    List<Object[]> countAtletasByAnoNascimento();

    // 4. Para o seu Gráfico de Colunas (Financeiro - Mensalidades Pendentes):
    List<Atleta> findByStatusPagamentoAndAtivoTrue(String status);
}