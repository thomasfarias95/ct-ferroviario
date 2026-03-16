package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.PagamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        Atleta salvo = repository.save(atleta);

        for (int i = 0; i < 12; i++) {
            Pagamento p = new Pagamento();
            p.setAtleta(salvo);
            p.setValor(new BigDecimal("100.00"));
            p.setPago(false);
            p.setDataVencimento(LocalDate.now().plusMonths(i).withDayOfMonth(diaVencimentoEscolhido));
            pagamentoRepository.save(p);
        }
        return salvo;
    }

    public List<Atleta> listarTodos() {
        return repository.findAll();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void rotinaDeCobranca() {
        LocalDate alvo = LocalDate.now().plusDays(7);
        List<Pagamento> proximosVencimentos = pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(alvo);

        for (Pagamento pagamento : proximosVencimentos) {
            if (pagamento.getDataVencimento().equals(alvo)) {
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
                .distinct()
                .collect(Collectors.toList());
    }
}