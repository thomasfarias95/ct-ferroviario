package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    // Método para a autenticação (Spring Security)
    UserDetails findByLogin(String login);

    // Método para busca por E-mail (Corrige o erro da image_1fda0e.png)
    Optional<Professor> findByEmail(String email);

    // Método para busca por Número Zempo (Corrige o erro da image_1fdccf.png)
    Optional<Professor> findByNumeroZempo(String numeroZempo);
}