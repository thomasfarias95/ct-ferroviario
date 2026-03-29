package com.cadastroatletas.Repository;

import com.cadastroatletas.Entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails; // Importante
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    // Busca técnica por Zempo (pode manter como Optional para lógica interna)
    Optional<Professor> findByNumeroZempo(String numeroZempo);

    // Ajuste para o Spring Security: Retorna UserDetails diretamente
    // Isso evita o erro no SecurityFilter e no AuthenticationService
    UserDetails findByEmail(String email);
}