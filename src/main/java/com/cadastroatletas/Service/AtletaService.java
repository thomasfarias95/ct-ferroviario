package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

// IMPORTS CORRETOS DO ITEXT 7 (Sincronizados com seu pom.xml)
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository repository;

    @Transactional
    public Atleta promoverAtleta(Long id, String novaGraduacao) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        atleta.setGraduacao(novaGraduacao);
        atleta.setDataUltimaGraduacao(LocalDate.now());
        return repository.save(atleta);
    }

    @Transactional
    public Atleta alterarStatusAtivo(Long id, boolean status) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));
        atleta.setAtivo(status);
        return repository.save(atleta);
    }

    public byte[] gerarRelatorioPdf(Long id) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Lógica iText 7: Writer -> PdfDocument -> Document
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("CT FERROVIÁRIO DE JUDÔ - RELATÓRIO TÉCNICO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());

            document.add(new Paragraph("--------------------------------------------------")
                    .setTextAlignment(TextAlignment.CENTER));

            // Validação de Nome
            String nomeExibicao = (atleta.getNomeCompleto() != null && !atleta.getNomeCompleto().isEmpty())
                    ? atleta.getNomeCompleto() :
                    (atleta.getNome() != null ? atleta.getNome() : "Atleta ID: " + id);

            document.add(new Paragraph("Nome: " + nomeExibicao));
            document.add(new Paragraph("Graduação: " + (atleta.getGraduacao() != null ? atleta.getGraduacao() : "Branca")));

            // Status Ativo
            boolean statusAtivo = (atleta.getAtivo() != null) ? atleta.getAtivo() : false;
            document.add(new Paragraph("Status: " + (statusAtivo ? "ATIVO" : "INATIVO")));

            document.add(new Paragraph("Última Graduação: " +
                    (atleta.getDataUltimaGraduacao() != null ? atleta.getDataUltimaGraduacao() : "Não registrada")));

            document.add(new Paragraph("Data de Emissão: " + LocalDate.now()));
            document.add(new Paragraph("--------------------------------------------------")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\nOss!")
                    .setItalic());

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            System.err.println("Erro ao gerar PDF no CT Ferroviário: " + e.getMessage());
            throw new RuntimeException("Erro ao gerar o PDF do atleta " + id, e);
        }
    }
}