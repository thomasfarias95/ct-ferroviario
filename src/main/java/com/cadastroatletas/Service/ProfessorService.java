package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Professor;
import com.cadastroatletas.Repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Professor salvar(Professor professor) {
        String senhaParaCriptografar;

        // 1. Verifica se o professor já enviou uma senha ou se precisamos gerar a padrão
        if (professor.getSenha() == null || professor.getSenha().trim().isEmpty()) {

            String nome = professor.getNomeCompleto().trim();
            String[] partes = nome.split("\\s+");
            String primeiroNome = partes[0].toLowerCase();

            // Regra de negócio: Limita o prefixo a 4 caracteres
            if (primeiroNome.length() > 4) {
                primeiroNome = primeiroNome.substring(0, 4);
            }

            // Ex: "Thomas" vira "thom2026"
            senhaParaCriptografar = primeiroNome + "2026";

        } else {
            senhaParaCriptografar = professor.getSenha().trim();
        }

        // 2. Trava de segurança (opcional, dependendo da sua regra de negócio)
        if (senhaParaCriptografar.length() > 20) { // Aumentei para 20 para dar mais flexibilidade
            throw new RuntimeException("A senha escolhida é muito longa.");
        }

        // 3. Criptografia antes de salvar
        professor.setSenha(passwordEncoder.encode(senhaParaCriptografar));

        return repository.save(professor);
    }

    public List<Professor> listarTodos() {
        return repository.findAll();
    }

    // Agora o repositório reconhece este método sem erros
    public Optional<Professor> buscarPorZempo(String zempo) {
        return repository.findByNumeroZempo(zempo);
    }

    // Removido o Cast manual, pois o repositório agora retorna Optional<Professor>
    public Optional<Professor> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }
}