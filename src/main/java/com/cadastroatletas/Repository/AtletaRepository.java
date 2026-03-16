package com.cadastroatletas.Repository;


import com.cadastroatletas.Entity.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AtletaRepository extends JpaRepository<Atleta, Long> {
    // Busca atletas pelo dia de vencimento para o robô de cobrança
    //List<Atleta> findByDiaVencimento(Integer dia);
    Optional<Atleta> findByEmail(String email);
}
