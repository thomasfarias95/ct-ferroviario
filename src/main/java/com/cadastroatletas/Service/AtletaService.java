package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
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

        // O método setGraduacao na Entity já atualiza a data, mas garantimos aqui também
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
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("CT FERROVIÁRIO DE JUDÔ - RELATÓRIO TÉCNICO"));
            document.add(new Paragraph("--------------------------------------------------"));

            // Validação de Nome (Prioriza Nome Completo, depois Nome, depois ID)
            String nomeExibicao = (atleta.getNomeCompleto() != null && !atleta.getNomeCompleto().isEmpty())
                    ? atleta.getNomeCompleto() :
                    (atleta.getNome() != null ? atleta.getNome() : "Atleta ID: " + id);

            document.add(new Paragraph("Nome: " + nomeExibicao));
            document.add(new Paragraph("Graduação: " + (atleta.getGraduacao() != null ? atleta.getGraduacao() : "Branca")));

            // Ajuste para usar o getAtivo() que definimos na Entity
            boolean statusAtivo = (atleta.getAtivo() != null) ? atleta.getAtivo() : false;
            document.add(new Paragraph("Status: " + (statusAtivo ? "ATIVO" : "INATIVO")));

            document.add(new Paragraph("Última Graduação: " +
                    (atleta.getDataUltimaGraduacao() != null ? atleta.getDataUltimaGraduacao() : "Não registrada")));

            document.add(new Paragraph("Data de Emissão: " + LocalDate.now()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("\nOss!"));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            // Log no console do Render para facilitar o debug se der erro
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            throw new RuntimeException("Erro ao gerar o PDF do atleta " + id, e);
        }
    }
}