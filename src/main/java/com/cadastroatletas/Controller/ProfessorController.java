package com.cadastroatletas.Controller;

import com.cadastroatletas.DTO.UsuarioDTO;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Professor;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Mudei a rota base para englobar todas as operações
@CrossOrigin(origins = "https://seu-projeto.vercel.app")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AtletaRepository atletaRepository; // Injetando o repositório de alunos

    // Cadastro unificado
    @PostMapping("/usuarios/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioDTO dto) {

        if ("PROFESSOR".equalsIgnoreCase(dto.getPapel())) {
            Professor p = new Professor();
            p.setNomeCompleto(dto.getNomeCompleto());
            p.setEmail(dto.getEmail());
            p.setSenha(dto.getNumeroZempo());
            p.setPapel("PROFESSOR");
            p.setGraduacao(dto.getGraduacao());

            return ResponseEntity.ok(professorService.salvar(p));

        } else if ("ALUNO".equalsIgnoreCase(dto.getPapel())) {
            Atleta a = new Atleta();
            a.setNomeCompleto(dto.getNomeCompleto());
            a.setEmail(dto.getEmail());
            a.setDiaVencimento(dto.getDiaVencimento());
            a.setStatusPagamento("PENDENTE");

            return ResponseEntity.ok(atletaRepository.save(a));
        }

        return ResponseEntity.badRequest().body("Papel inválido. Use PROFESSOR ou ALUNO.");
    }

    // Mantendo os outros métodos de Professor
    @GetMapping("/professores")
    public List<Professor> listar() {
        return professorService.listarTodos();
    }
}