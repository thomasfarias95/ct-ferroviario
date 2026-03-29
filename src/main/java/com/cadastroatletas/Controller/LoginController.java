package com.cadastroatletas.Controller;

import com.cadastroatletas.DTO.LoginDTO;
import com.cadastroatletas.Entity.Professor; // AJUSTADO
import com.cadastroatletas.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data) {
        try {
            // Valida as credenciais do Professor
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getSenha());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            // IMPORTANTE: Agora fazemos o cast para Professor
            Professor professor = (Professor) auth.getPrincipal();

            // Gera o token usando a entidade correta
            var token = tokenService.generateToken(professor);

            // Retornamos os dados para o Next.js
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "nome", professor.getNomeCompleto(),
                    "papel", professor.getPapel() != null ? professor.getPapel() : "PROFESSOR",
                    "foto", professor.getFotoUrl() != null ? professor.getFotoUrl() : ""
            ));

        } catch (Exception e) {
            System.out.println("Falha no login para " + data.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(401).body("E-mail ou senha incorretos.");
        }
    }
}