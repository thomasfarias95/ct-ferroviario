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

// Imports do iText para geração de PDF real
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
@Transactional
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
    public ResponseEntity<?> atualizarAtleta(@PathVariable Long id, @RequestBody Atleta dados) {
        try {
            if (dados.getStatusPagamento() != null && "EM_DIA".equals(dados.getStatusPagamento())) {
                pagamentoService.confirmarPagamentoPeloAtleta(id);
                return ResponseEntity.ok().build();
            }

            return atletaRepository.findById(id)
                    .map(atleta -> {
                        if (dados.getAtivo() != null) atleta.setAtivo(dados.getAtivo());
                        if (dados.getGraduacao() != null) atleta.setGraduacao(dados.getGraduacao());
                        atletaRepository.saveAndFlush(atleta);
                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());

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

            // Prepara o fluxo de bytes para o PDF real
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Montando o conteúdo do documento
            document.add(new Paragraph("CT FERROVIÁRIO - FICHA TÉCNICA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20));

            document.add(new Paragraph("\n")); // Espaçamento
            document.add(new Paragraph("Nome do Atleta: " + (atleta.getNomeCompleto() != null ? atleta.getNomeCompleto() : atleta.getNome())));
            document.add(new Paragraph("Graduação atual: " + atleta.getGraduacao()));
            document.add(new Paragraph("Turno: " + atleta.getTurno()));
            document.add(new Paragraph("Situação: " + (atleta.getAtivo() != false ? "ATIVO" : "INATIVO")));
            document.add(new Paragraph("Vencimento Mensalidade: Dia " + (atleta.getDiaVencimento() != null ? atleta.getDiaVencimento() : "28")));

            document.add(new Paragraph("\n\n__________________________________")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Assinatura do Sensei")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10));

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            // Configura os headers para o navegador entender que é um PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Ficha_Tecnica_" + atleta.getNome() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}