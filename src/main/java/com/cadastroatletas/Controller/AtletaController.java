package com.cadastroatletas.Controller;

import com.cadastroatletas.Service.PagamentoService;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin("*")
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService;

    // ESTE É O MÉTODO QUE VAI FAZER OS DADOS APARECEREM NO VERCEL
    @GetMapping
    public ResponseEntity<List<Atleta>> listarTodos() {
        // Ele vai usar o findAll() que já existe por padrão no JpaRepository
        return ResponseEntity.ok(atletaRepository.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarStatusPagamento(@PathVariable Long id, @RequestBody Atleta dados) {
        // ... (seu código de patch atual)
        try {
            if ("EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                return ResponseEntity.ok().build();
            }
            return atletaRepository.findById(id)
                    .map(atleta -> {
                        if (dados.getAtivo() != null) atleta.setAtivo(dados.getAtivo());
                        atletaRepository.save(atleta);
                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar: " + e.getMessage());
        }
    }
}