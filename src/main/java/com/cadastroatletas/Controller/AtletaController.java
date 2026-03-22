package com.cadastroatletas.Controller;

import com.cadastroatletas.Service.PagamentoService;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin("*")
@Transactional // Garante que toda operação no banco seja confirmada (commit)
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<Atleta>> listarTodos() {
        return ResponseEntity.ok(atletaRepository.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarStatusPagamento(@PathVariable Long id, @RequestBody Atleta dados) {
        try {
            // Lógica para dar baixa no pagamento (O "Ovo de Ouro")
            if (dados.getStatusPagamento() != null && "EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                return ResponseEntity.ok().build();
            }

            // Lógica para ativar/desativar atleta ou outras atualizações
            return atletaRepository.findById(id)
                    .map(atleta -> {
                        if (dados.getAtivo() != null) {
                            atleta.setAtivo(dados.getAtivo());
                        }
                        // Usando saveAndFlush para persistência imediata
                        atletaRepository.saveAndFlush(atleta);
                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            // VITAL: Isso faz o erro detalhado aparecer no console do Render
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno no servidor: " + e.getMessage());
        }
    }
}