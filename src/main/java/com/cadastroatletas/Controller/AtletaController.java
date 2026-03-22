package com.cadastroatletas.Controller;

import com.cadastroatletas.Service.PagamentoService;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin("*") // IMPORTANTE para evitar erro de conexão da Vercel
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService; // Injetando o novo Service

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarStatusPagamento(@PathVariable Long id, @RequestBody Atleta dados) {
        try {
            // Se o Front-end enviou "EM_DIA", chamamos a lógica de confirmação
            if ("EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                return ResponseEntity.ok().build();
            }

            // Caso seja outra atualização (como desativar atleta), você mantém a lógica antiga
            return atletaRepository.findById(id)
                    .map(atleta -> {
                        if (dados.getAtivo() != null) atleta.setAtivo(dados.getAtivo());
                        atletaRepository.save(atleta);
                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar baixa: " + e.getMessage());
        }
    }
}