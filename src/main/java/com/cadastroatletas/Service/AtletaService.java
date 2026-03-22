package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.PagamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

// --- NOVAS IMPORTAÇÕES PARA O PDF ---
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
// ------------------------------------

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository repository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Transactional
    public Atleta salvarComMensalidades(Atleta atleta, int diaVencimentoEscolhido) {
        // Garante que novos atletas comecem ativos
        atleta.setAtivo(true);
        Atleta salvo = repository.save(atleta);

        for (int i = 0; i < 12; i++) {
            Pagamento p = new Pagamento();
            p.setAtleta(salvo);
            p.setValor(new BigDecimal("100.00"));
            p.setPago(false);
            // Ajuste para evitar erro se o dia escolhido for 31 e o mês tiver 30
            p.setDataVencimento(LocalDate.now().plusMonths(i).withDayOfMonth(Math.min(diaVencimentoEscolhido, 28)));
            pagamentoRepository.save(p);
        }
        return salvo;
    }

    // --- NOVAS FUNCIONALIDADES ---

    @Transactional
    public Atleta promoverAtleta(Long id, String novaGraduacao) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        atleta.setGraduacao(novaGraduacao); // O setter na Entity já atualiza a data de graduação
        return repository.save(atleta);
    }

    @Transactional
    public Atleta alterarStatusAtivo(Long id, boolean status) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        atleta.setAtivo(status);
        return repository.save(atleta);
    }

    // Listar apenas quem está treinando (Ativos)
    public List<Atleta> listarTodos() {
        return repository.findByAtivoTrue();
    }

    // --- MÉTODO PARA GERAÇÃO DE PDF (O QUE RESOLVE O ERRO NO CONTROLLER) ---
    public byte[] gerarRelatorioPdf(Long id) {
        Atleta atleta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Cabeçalho e informações no PDF
            document.add(new Paragraph("RELATÓRIO TÉCNICO - CT FERROVIÁRIO"));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Nome: " + atleta.getNomeCompleto()));
            document.add(new Paragraph("Graduação Atual: " + atleta.getGraduacao()));
            document.add(new Paragraph("Turno: " + atleta.getTurno()));
            document.add(new Paragraph("Status no Sistema: " + (atleta.isAtivo() ? "ATIVO" : "INATIVO")));
            document.add(new Paragraph("Data de Emissão: " + LocalDate.now()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Oss!"));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o PDF do atleta", e);
        }
    }

    // --- AJUSTE NA ROTINA DE COBRANÇA ---

    @Scheduled(cron = "0 0 8 * * *")
    public void rotinaDeCobranca() {
        LocalDate alvo = LocalDate.now().plusDays(7);
        List<Pagamento> proximosVencimentos = pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(alvo);

        for (Pagamento pagamento : proximosVencimentos) {
            if (pagamento.getDataVencimento().equals(alvo) && pagamento.getAtleta().isAtivo()) {
                enviarNotificacaoWhatsApp(pagamento.getAtleta(), pagamento);
            }
        }
    }

    private void enviarNotificacaoWhatsApp(Atleta atleta, Pagamento pagamento) {
        String msg = "Olá, tudo bem? Passando para informar sobre o vencimento da mensalidade de "
                + atleta.getNome() + " no dia " + pagamento.getDataVencimento()
                + ". Caso já tenha pago, por favor desconsiderar.";

        System.out.println("ENVIANDO WHATSAPP PARA: " + atleta.getTelefone());
        System.out.println("MENSAGEM: " + msg);
    }

    public void darBaixaManual(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        pagamento.setPago(true);
        pagamento.setDataPagamento(LocalDate.now());
        pagamentoRepository.save(pagamento);
    }

    public List<Pagamento> buscarPagamentosPorAtleta(Long atletaId) {
        return pagamentoRepository.findByAtletaId(atletaId);
    }

    public List<Atleta> listarInadimplentes() {
        List<Pagamento> atrasados = pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(LocalDate.now());

        return atrasados.stream()
                .map(Pagamento::getAtleta)
                .filter(Atleta::isAtivo)
                .distinct()
                .collect(Collectors.toList());
    }
}