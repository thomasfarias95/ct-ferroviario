package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    public List<Pagamento> listarVencimentosProximos(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(dataLimite);
    }

    public List<Pagamento> listarInadimplentes() {
        return pagamentoRepository.findByPagoFalseAndDataVencimentoBefore(LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void renovarPagamentosMensais() {
        List<Atleta> atletasAtivos = atletaRepository.findAll()
                .stream()
                .filter(atleta -> atleta.getAtivo() != null && atleta.getAtivo())
                .toList();

        for (Atleta atleta : atletasAtivos) {
            atleta.setStatusPagamento("PENDENTE");
            atletaRepository.saveAndFlush(atleta);
            gerarNovoPagamento(atleta);
        }
    }

    public void criarPagamento(Long atletaId) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));
        gerarNovoPagamento(atleta);
    }

    private void gerarNovoPagamento(Atleta atleta) {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setAtleta(atleta);
        novoPagamento.setValor(new BigDecimal("100.00"));
        novoPagamento.setPago(false);

        int diaVencimento = (atleta.getDiaVencimento() != null) ? atleta.getDiaVencimento() : 10;
        LocalDate vencimento = LocalDate.now().withDayOfMonth(diaVencimento);

        if (vencimento.isBefore(LocalDate.now())) {
            vencimento = vencimento.plusMonths(1);
        }

        novoPagamento.setDataVencimento(vencimento);
        pagamentoRepository.saveAndFlush(novoPagamento);
    }

    @Transactional
    public Pagamento confirmarPagamentoPeloAtleta(Long atletaId) {
        // 1. Busca o atleta
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        // 2. Busca o pagamento pendente com proteção contra nulos
        Optional<Pagamento> pagamentoPendente = pagamentoRepository.findAll().stream()
                .filter(pag -> pag.getAtleta() != null && pag.getAtleta().getId().equals(atletaId))
                .filter(pag -> !pag.isPago())
                .findFirst();

        // Se encontrar, atualiza. Se não encontrar (ex: já está pago), apenas garante que o Atleta está EM_DIA
        if (pagamentoPendente.isPresent()) {
            Pagamento p = pagamentoPendente.get();
            p.setPago(true);
            p.setDataPagamento(LocalDate.now());
            pagamentoRepository.saveAndFlush(p);
        }

        // 3. Garante o status no Atleta (Isso resolve o problema do F5)
        atleta.setStatusPagamento("EM_DIA");
        atletaRepository.saveAndFlush(atleta);

        return pagamentoPendente.orElse(null);
    }

    @Transactional
    public Pagamento confirmarPagamento(Long pagamentoId) {
        Pagamento p = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        p.setPago(true);
        p.setDataPagamento(LocalDate.now());
        pagamentoRepository.saveAndFlush(p);

        Atleta atleta = p.getAtleta();
        if (atleta != null) {
            atleta.setStatusPagamento("EM_DIA");
            atletaRepository.saveAndFlush(atleta);
        }

        return p;
    }
}