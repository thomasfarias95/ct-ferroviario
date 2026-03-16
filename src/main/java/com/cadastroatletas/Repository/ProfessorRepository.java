package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    // Busca original que você já tinha
    Optional<Professor> findByNumeroZempo(String numeroZempo);

    // Novo método para a lógica de login
    Optional<Professor> findByEmail(String email);


}
