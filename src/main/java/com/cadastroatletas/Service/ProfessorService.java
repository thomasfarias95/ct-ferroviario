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
        return repository.save(professor);
    }

    public List<Professor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Professor> buscarPorZempo(String zempo) {
        return repository.findByNumeroZempo(zempo);
    }
}
