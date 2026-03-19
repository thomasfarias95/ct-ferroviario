package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {

    Optional<Atleta> findByEmail(String email);

    List<Atleta> findByTurnoAndAtivoTrue(String turno);

    @Query("SELECT a.sexo, COUNT(a) FROM Atleta a WHERE a.ativo = true GROUP BY a.sexo")
    List<Object[]> countAtletasBySexo();

    @Query("SELECT EXTRACT(YEAR FROM a.dataNascimento), COUNT(a) FROM Atleta a GROUP BY EXTRACT(YEAR FROM a.dataNascimento)")
    List<Object[]> countAtletasByAnoNascimento();

    // --- NOVA CONSULTA PARA AUTOMAÇÃO DE WHATSAPP ---
    // Busca atletas ativos que vencem em X dias e que ainda não foram notificados hoje
    @Query(value = "SELECT * FROM atletas WHERE ativo = true AND dia_vencimento = " +
            "EXTRACT(DAY FROM (CURRENT_DATE + CAST(:dias || ' days' AS INTERVAL))) " +
            "AND (ultima_notificacao IS NULL OR ultima_notificacao < CURRENT_DATE)",
            nativeQuery = true)
    List<Atleta> findAtletasParaNotificar(@Param("dias") int dias);
}