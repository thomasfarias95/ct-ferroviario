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
        atleta.setGraduacao(novaGraduacao);
        atleta.setDataUltimaGraduacao(LocalDate.now()); // Atualiza a data da troca
        return repository.save(atleta);
    }

    @Transactional
    public Atleta alterarStatusAtivo(Long id, boolean status) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));
        atleta.setAtivo(status);
        return repository.save(atleta);
    }

    // MÉTODO REVISADO COM A LINHA DE SEGURANÇA PARA O NOME
    public byte[] gerarRelatorioPdf(Long id) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("RELATÓRIO TÉCNICO - CT FERROVIÁRIO"));
            document.add(new Paragraph("--------------------------------------------------"));

            // LINHA SOLICITADA: Validação para evitar NullPointerException no PDF
            // Tenta usar getNomeCompleto(), se for nulo tenta getNome(), se ambos nulos diz "Não informado"
            String nomeExibicao = (atleta.getNomeCompleto() != null) ? atleta.getNomeCompleto() :
                    (atleta.getNome() != null ? atleta.getNome() : "Não informado");

            document.add(new Paragraph("Nome: " + nomeExibicao));

            document.add(new Paragraph("Graduação: " + (atleta.getGraduacao() != null ? atleta.getGraduacao() : "Branca")));
            document.add(new Paragraph("Status: " + (atleta.isAtivo() ? "ATIVO" : "INATIVO")));
            document.add(new Paragraph("Data de Emissão: " + LocalDate.now()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Oss!"));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o PDF do atleta " + id, e);
        }
    }
}