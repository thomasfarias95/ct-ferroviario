package com.cadastroatletas.Controller;

import com.cadastroatletas.DTO.LoginDTO;
import com.cadastroatletas.DTO.UsuarioResponseDTO;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Professor;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "https://seu-projeto.vercel.app") // Certifique-se que seu Next.js está na porta 3000
@RestController
@RequestMapping("/api/auth") // Certifique-se que o Next está na 3000
public class LoginController {

    @Autowired
    private ProfessorRepository profRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {

        // Log para ajudar a debugar o que está chegando
        System.out.println("Tentativa de login para: " + login.getEmail());

        // 1. Tentar validar como Professor
        Optional<Professor> professorOpt = profRepository.findByEmail(login.getEmail());
        if (professorOpt.isPresent()) {
            Professor p = professorOpt.get();
            if (p.getSenha() != null && p.getSenha().trim().equals(login.getSenha().trim())) {
                System.out.println("Professor autenticado com sucesso!");
                return ResponseEntity.ok(new UsuarioResponseDTO(p.getNomeCompleto(), "PROFESSOR", p.getEmail(), p.getFotoUrl()));
            }
        }

        // 2. Tentar validar como Aluno
        Optional<Atleta> atletaOpt = atletaRepository.findByEmail(login.getEmail());
        if (atletaOpt.isPresent()) {
            Atleta a = atletaOpt.get();
            if (a.getSenha() != null && a.getSenha().trim().equals(login.getSenha().trim())) {
                System.out.println("Aluno autenticado com sucesso!");
                return ResponseEntity.ok(new UsuarioResponseDTO(a.getNomeCompleto(), "ALUNO", a.getEmail(), null));
            }
            System.out.println("Tentativa para: " + login.getEmail());

            Optional<Professor> prof = profRepository.findByEmail(login.getEmail());
            if(prof.isPresent()) {
                System.out.println("Professor encontrado! Senha no banco: " + prof.get().getSenha());
                // ... restante da lógica
            } else {
                System.out.println("Usuário não encontrado.");
            }
        }

        // Se chegar aqui, nenhum dos dois foi validado
        System.out.println("Falha na autenticação para: " + login.getEmail());
        return ResponseEntity.status(401).body("E-mail ou senha incorretos.");


    }
}
