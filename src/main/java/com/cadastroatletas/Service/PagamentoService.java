package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    public void renovarPagamentosMensais() {
        // CORREÇÃO: Usando getAtivo() e garantindo que não seja nulo antes de filtrar
        List<Atleta> atletasAtivos = atletaRepository.findAll()
                .stream()
                .filter(atleta -> atleta.getAtivo() != null && atleta.getAtivo())
                .toList();

        for (Atleta atleta : atletasAtivos) {
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

        // Segurança: Se o dia de vencimento for nulo na Entity, assume dia 10
        int diaVencimento = (atleta.getDiaVencimento() != null) ? atleta.getDiaVencimento() : 10;
        LocalDate vencimento = LocalDate.now().withDayOfMonth(diaVencimento);

        if (vencimento.isBefore(LocalDate.now())) {
            vencimento = vencimento.plusMonths(1);
        }

        novoPagamento.setDataVencimento(vencimento);
        pagamentoRepository.save(novoPagamento);
    }

    public Pagamento confirmarPagamento(Long pagamentoId) {
        Pagamento p = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        p.setPago(true);
        p.setDataPagamento(LocalDate.now());
        return pagamentoRepository.save(p);
    }
}