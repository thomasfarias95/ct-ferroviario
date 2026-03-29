package com.cadastroatletas.Controller;

import com.cadastroatletas.Service.PagamentoService;
import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin("*")
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Atleta>> listarTodos() {
        return ResponseEntity.ok(atletaRepository.findAll());
    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarAtleta(@PathVariable Long id, @RequestBody Atleta dados) {
        try {
            Atleta atleta = atletaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

            // --- 1. Processa Senha com Trava de 8 Caracteres ---
            if (dados.getSenha() != null && !dados.getSenha().trim().isEmpty()) {
                String senhaLimpa = dados.getSenha().trim();

                if (senhaLimpa.length() > 8) {
                    return ResponseEntity.badRequest().body("Erro: A senha não pode ter mais de 8 caracteres.");
                }

                atleta.setSenha(passwordEncoder.encode(senhaLimpa));
            }

            // 2. Processa Pagamento
            if (dados.getStatusPagamento() != null && "EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                atleta = atletaRepository.findById(id).get();
            }

            // 3. Outros campos
            if (dados.getNome() != null) atleta.setNome(dados.getNome());
            if (dados.getAtivo() != null) atleta.setAtivo(dados.getAtivo());
            if (dados.getGraduacao() != null) atleta.setGraduacao(dados.getGraduacao());
            if (dados.getTurno() != null) atleta.setTurno(dados.getTurno());

            atletaRepository.saveAndFlush(atleta);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    // --- Reset de Senha com a mesma trava ---
    @PostMapping("/{id}/reset-password")
    @Transactional
    public ResponseEntity<String> resetarSenha(@PathVariable Long id, @RequestBody String novaSenha) {
        if (novaSenha == null || novaSenha.trim().length() > 8) {
            return ResponseEntity.badRequest().body("Senha inválida ou maior que 8 caracteres.");
        }
        Atleta atleta = atletaRepository.findById(id).orElseThrow();
        atleta.setSenha(passwordEncoder.encode(novaSenha.trim()));
        atletaRepository.save(atleta);
        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }

    @GetMapping("/{id}/relatorio-pdf")
    public ResponseEntity<byte[]> gerarRelatorioPdf(@PathVariable Long id) {
        // ... (Seu código do iTextPdf permanece o mesmo)
        return ResponseEntity.ok().build();
    }
}