package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByAtletaId(Long atletaId);

    List<Pagamento> findByPagoFalseAndDataVencimentoLessThanEqual(LocalDate data);
    // No PagamentoRepository.java
    List<Pagamento> findByPagoFalseAndDataVencimentoBefore(LocalDate data);

    @Query("SELECT p FROM Pagamento p JOIN FETCH p.atleta a WHERE p.dataPagamento BETWEEN :inicio AND :fim")
    List<Pagamento> buscarPagamentosDoMes(LocalDate inicio, LocalDate fim);
}
