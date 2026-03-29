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

            // 1. Processa Pagamento (se enviado)
            if (dados.getStatusPagamento() != null && "EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                // Após o serviço, recarregamos o atleta para garantir que temos os dados pós-pagamento
                atleta = atletaRepository.findById(id).get();
            }

            // 2. Processa Status Ativo/Inativo
            if (dados.getAtivo() != null) {
                atleta.setAtivo(dados.getAtivo());
            }

            // 3. Processa Graduação (Aciona a lógica de data automática que criamos na Entity)
            if (dados.getGraduacao() != null) {
                atleta.setGraduacao(dados.getGraduacao());
            }

            atletaRepository.saveAndFlush(atleta);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/relatorio-pdf")
    public ResponseEntity<byte[]> gerarRelatorioPdf(@PathVariable Long id) {
        try {
            Atleta atleta = atletaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("CT FERROVIÁRIO - FICHA TÉCNICA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(22));

            document.add(new Paragraph("__________________________________________________________")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("DADOS CADASTRAIS").setBold().setFontSize(14));
            document.add(new Paragraph("Nome Completo: " + (atleta.getNomeCompleto() != null ? atleta.getNomeCompleto() : atleta.getNome())));
            document.add(new Paragraph("Graduação: " + atleta.getGraduacao()));
            document.add(new Paragraph("Turno de Treino: " + atleta.getTurno()));
            document.add(new Paragraph("Status de Matrícula: " + (atleta.getAtivo() != false ? "ATIVA" : "SUSPENSA")));
            document.add(new Paragraph("Dia de Vencimento: Dia " + (atleta.getDiaVencimento() != null ? atleta.getDiaVencimento() : "10")));
            document.add(new Paragraph("Situação Financeira: " + (atleta.getStatusPagamento() != null ? atleta.getStatusPagamento() : "PENDENTE")));

            document.add(new Paragraph("\n\n\n\n_________________________________________")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Assinatura do Responsável / Sensei")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic());

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            String nomeLimpo = (atleta.getNome() != null ? atleta.getNome() : "Atleta").replaceAll("\\s+", "_");
            String fileName = "Ficha_" + nomeLimpo + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}