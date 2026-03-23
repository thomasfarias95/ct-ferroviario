package com.cadastroatletas.Controller;

import com.cadastroatletas.Service.PagamentoService;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin("*")
@Transactional
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService;

    // Listagem principal para o Dashboard
    @GetMapping
    public ResponseEntity<List<Atleta>> listarTodos() {
        return ResponseEntity.ok(atletaRepository.findAll());
    }

    // Endpoint do "Ovo de Ouro": Baixa de pagamento e Ativação/Desativação
    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarStatusPagamento(@PathVariable Long id, @RequestBody Atleta dados) {
        try {
            // Se o front enviar EM_DIA, aciona a regra de negócio do PagamentoService
            if (dados.getStatusPagamento() != null && "EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                return ResponseEntity.ok().build();
            }

            // Lógica para ativar/desativar atleta (Botão SUSPENDER/ATIVAR)
            return atletaRepository.findById(id)
                    .map(atleta -> {
                        if (dados.getAtivo() != null) {
                            atleta.setAtivo(dados.getAtivo());
                        }
                        atletaRepository.saveAndFlush(atleta);
                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno no servidor: " + e.getMessage());
        }
    }

    // ROTA DO PDF TÉCNICO (Resolve o Erro 404 do botão 🥋)
    @GetMapping("/{id}/relatorio-pdf")
    public ResponseEntity<byte[]> gerarRelatorioPdf(@PathVariable Long id) {
        try {
            Atleta atleta = atletaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

            // Aqui você deve chamar sua lógica de PDF.
            // Exemplo genérico:
            byte[] pdfBytes = "Relatorio Tecnico do Atleta".getBytes(); // Substituir pela lógica real de PDF

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_" + atleta.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).build();
        }
    }
}