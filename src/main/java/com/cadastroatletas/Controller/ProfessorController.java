package com.cadastroatletas.Controller;

import com.cadastroatletas.DTO.UsuarioDTO;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Professor;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Service.ProfessorService; // IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService; // Usando o Service agora

    @Autowired
    private AtletaRepository atletaRepository;

    @PostMapping("/usuarios/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioDTO dto) {

        if ("PROFESSOR".equalsIgnoreCase(dto.getPapel())) {
            Professor p = new Professor();
            p.setNomeCompleto(dto.getNomeCompleto());
            p.setEmail(dto.getEmail());
            p.setNumeroZempo(dto.getNumeroZempo());
            p.setGraduacao(dto.getGraduacao());
            p.setPapel("PROFESSOR");

            // Se o DTO não trouxer senha, o Service vai gerar o "nome2026"
            p.setSenha(dto.getSenha());

            // CHAMADA CRUCIAL: Aqui ele vai lá no Service, faz o split, o e o toLowerCase
            return ResponseEntity.ok(professorService.salvar(p));

        } else if ("ALUNO".equalsIgnoreCase(dto.getPapel())) {
            Atleta a = new Atleta();
            a.setNomeCompleto(dto.getNomeCompleto());
            a.setEmail(dto.getEmail());
            a.setGraduacao(dto.getGraduacao());
            a.setAtivo(true);
            a.setStatusPagamento("PENDENTE");

            return ResponseEntity.ok(atletaRepository.save(a));
        }

        return ResponseEntity.badRequest().body("Papel inválido. Use PROFESSOR ou ALUNO.");
    }

    @GetMapping("/professores")
    public List<Professor> listarProfessores() {
        return professorService.listarTodos();
    }
}