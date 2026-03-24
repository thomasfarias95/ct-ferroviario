package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {


    List<Pagamento> findByAtletaId(Long atletaId);


    Optional<Pagamento> findFirstByAtletaIdAndPagoFalseOrderByDataVencimentoAsc(Long atletaId);


    List<Pagamento> findByPagoFalseAndDataVencimentoLessThanEqual(LocalDate data);


    List<Pagamento> findByPagoFalseAndDataVencimentoBefore(LocalDate data);

    @Query("SELECT p FROM Pagamento p JOIN FETCH p.atleta a WHERE p.dataPagamento BETWEEN :inicio AND :fim")
    List<Pagamento> buscarPagamentosDoMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}