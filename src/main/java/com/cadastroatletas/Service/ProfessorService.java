package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Professor;
import com.cadastroatletas.Repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository repository;

    public Professor salvar(Professor professor) {
        // Lógica para gerar senha automática e amigável
        if (professor.getSenha() == null || professor.getSenha().trim().isEmpty()) {

            // 1. Pega o nome completo e remove espaços extras
            String nome = professor.getNomeCompleto().trim();

            // 2. Extrai apenas o primeiro nome (ex: "Aldisio" de "Aldisio Silva")
            String primeiroNome = nome.split("\\s+")[0].toLowerCase();

            // 3. Define a senha padrão: nome + 2026 (Fácil de decorar e digitar)
            // Como não usa criptografia, salva o texto puro direto
            professor.setSenha(primeiroNome + "2026");
        }

        return repository.save(professor);
    }

    public List<Professor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Professor> buscarPorZempo(String zempo) {
        return repository.findByNumeroZempo(zempo);
    }

    // Método extra para ajudar no Login futuro se precisar buscar por e-mail
    public Optional<Professor> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }
}