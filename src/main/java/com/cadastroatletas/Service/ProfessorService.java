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

        // Verifica se o professor já enviou uma senha ou se precisamos gerar a padrão
        if (professor.getSenha() == null || professor.getSenha().trim().isEmpty()) {

            // 1. Limpa espaços do nome completo
            String nome = professor.getNomeCompleto().trim();

            // 2. Quebra o nome em partes (Array de Strings)
            String[] partes = nome.split("\\s+");

            // 3. CORREÇÃO: Pegamos a primeira posição para poder usar o toLowerCase()
            String primeiroNome = partes[0].toLowerCase();

            // 4. Regra de negócio: Limita o prefixo a 4 caracteres
            if (primeiroNome.length() > 4) {
                primeiroNome = primeiroNome.substring(0, 4);
            }

            // Resultado Ex: "thomas" vira "thom2026"
            senhaParaCriptografar = primeiroNome + "2026";

        } else {
            // Se o professor já definiu uma senha, usamos a dele (removendo espaços)
            senhaParaCriptografar = professor.getSenha().trim();
        }

        // 5. Trava de segurança: Senha não pode ser maior que 8 caracteres para o padrão do sistema
        if (senhaParaCriptografar.length() > 8) {
            throw new RuntimeException("A senha não pode ter mais de 8 caracteres.");
        }

        // 6. CRUCIAL: Criptografa a senha antes de persistir no PostgreSQL
        professor.setSenha(passwordEncoder.encode(senhaParaCriptografar));

        return repository.save(professor);
    }

    public List<Professor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Professor> buscarPorZempo(String zempo) {
        return repository.findByNumeroZempo(zempo);
    }

    public Optional<Professor> buscarPorEmail(String email) {
        // Cast necessário para garantir o retorno do tipo Professor
        return Optional.ofNullable((Professor) repository.findByEmail(email));
    }
}