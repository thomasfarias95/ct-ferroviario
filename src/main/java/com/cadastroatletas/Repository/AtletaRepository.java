package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {

    // AJUSTE PARA SEGURANÇA: Retorna UserDetails diretamente para o SecurityFilter
    UserDetails findByEmail(String email);

    // Mantemos para listagens rápidas de treino
    List<Atleta> findByAtivoTrue();

    // Útil para auditoria de alunos que saíram
    List<Atleta> findByAtivoFalse();

    // Filtra por turno para organizar as aulas
    List<Atleta> findByTurnoAndAtivoTrue(String turno);

    // Estatísticas para o Power BI: Apenas ativos
    @Query("SELECT a.sexo, COUNT(a) FROM Atleta a WHERE a.ativo = true GROUP BY a.sexo")
    List<Object[]> countAtletasBySexo();

    // Distribuição por idade (Ativos)
    @Query("SELECT EXTRACT(YEAR FROM a.dataNascimento), COUNT(a) FROM Atleta a WHERE a.ativo = true GROUP BY EXTRACT(YEAR FROM a.dataNascimento)")
    List<Object[]> countAtletasByAnoNascimento();

    // --- AUTOMAÇÃO DE WHATSAPP ---
    @Query(value = "SELECT * FROM atletas WHERE ativo = true " +
            "AND dia_vencimento = EXTRACT(DAY FROM (CURRENT_DATE + CAST(:dias || ' days' AS INTERVAL))) " +
            "AND (ultima_notificacao IS NULL OR ultima_notificacao < CURRENT_DATE)",
            nativeQuery = true)
    List<Atleta> findAtletasParaNotificar(@Param("dias") int dias);
}