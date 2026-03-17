package com.cadastroatletas.Controller;

import com.cadastroatletas.DTO.UsuarioDTO;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Professor;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cadastro")
@CrossOrigin(origins = "https://seu-projeto.vercel.app")
public class CadastroController {

    @Autowired private ProfessorRepository profRepo;
    @Autowired private AtletaRepository alunoRepo;

    // Rota para listar (A que estava faltando)
    @GetMapping("/atletas")
    public ResponseEntity<?> listarAtletas() {
        return ResponseEntity.ok(alunoRepo.findAll());
    }

    @PostMapping("/novo")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioDTO dto) {
        // ... (seu código de cadastro igual ao que você já tem)
        if ("ALUNO".equalsIgnoreCase(dto.getPapel())) {
            Atleta a = new Atleta();
            a.setNomeCompleto(dto.getNomeCompleto());
            a.setEmail(dto.getEmail());
            a.setDiaVencimento(dto.getDiaVencimento());
            a.setStatusPagamento("PENDENTE");
            alunoRepo.save(a);
            return ResponseEntity.ok("Aluno cadastrado!");
        }
        return ResponseEntity.badRequest().build();
    }

    // Rota de baixa
    @PatchMapping("/atletas/{id}/baixa-pagamento")
    public ResponseEntity<?> darBaixaPagamento(@PathVariable Long id) {
        return alunoRepo.findById(id).map(a -> {
            a.setStatusPagamento("PAGO");
            alunoRepo.save(a);
            return ResponseEntity.ok("Baixa realizada!");
        }).orElse(ResponseEntity.notFound().build());
    }
}