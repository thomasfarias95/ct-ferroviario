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
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    // LISTAR VENCIMENTOS PRÓXIMOS
    public List<Pagamento> listarVencimentosProximos(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(dataLimite);
    }

    // LISTAR QUEM ESTÁ ATRASADO
    public List<Pagamento> listarInadimplentes() {
        return pagamentoRepository.findByPagoFalseAndDataVencimentoBefore(LocalDate.now());
    }

    // ROTINA AUTOMÁTICA: Todo dia 1º do mês vira tudo para PENDENTE
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

    // GERAÇÃO DE NOVO LANÇAMENTO FINANCEIRO
    @Transactional
    public void gerarNovoPagamento(Atleta atleta) {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setAtleta(atleta);
        novoPagamento.setValor(new BigDecimal("100.00")); // Valor padrão da mensalidade do CT
        novoPagamento.setPago(false);

        int diaVencimento = (atleta.getDiaVencimento() != null) ? atleta.getDiaVencimento() : 10;
        LocalDate hoje = LocalDate.now();

        // Proteção contra dias inexistentes (ex: dia 31 em fevereiro)
        int ultimoDiaMes = hoje.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        int diaAjustado = Math.min(diaVencimento, ultimoDiaMes);

        LocalDate vencimento = hoje.withDayOfMonth(diaAjustado);

        // Se o dia de vencimento já passou este mês, lança para o próximo
        if (vencimento.isBefore(hoje)) {
            vencimento = vencimento.plusMonths(1);
        }

        novoPagamento.setDataVencimento(vencimento);
        pagamentoRepository.saveAndFlush(novoPagamento);
    }

    // CONFIRMAÇÃO DE PAGAMENTO (USADO PELO DASHBOARD)
    @Transactional
    public void confirmarPagamentoPeloAtleta(Long atletaId) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        // Busca o pagamento em aberto mais antigo deste atleta (Otimizado)
        // Nota: Ideal ter um método findFirstByAtletaIdAndPagoFalse no Repository
        Optional<Pagamento> pagamentoPendente = pagamentoRepository.findAll().stream()
                .filter(pag -> pag.getAtleta() != null && pag.getAtleta().getId().equals(atletaId))
                .filter(pag -> !pag.isPago())
                .findFirst();

        if (pagamentoPendente.isPresent()) {
            Pagamento p = pagamentoPendente.get();
            p.setPago(true);
            p.setDataPagamento(LocalDate.now());
            pagamentoRepository.saveAndFlush(p);
        }

        // Sincroniza o status no objeto Atleta para o Front-End
        atleta.setStatusPagamento("EM_DIA");
        atletaRepository.saveAndFlush(atleta);
    }

    // CONFIRMAÇÃO POR ID DO TÍTULO
    @Transactional
    public Pagamento confirmarPagamento(Long pagamentoId) {
        Pagamento p = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        p.setPago(true);
        p.setDataPagamento(LocalDate.now());
        pagamentoRepository.saveAndFlush(p);

        if (p.getAtleta() != null) {
            p.getAtleta().setStatusPagamento("EM_DIA");
            atletaRepository.saveAndFlush(p.getAtleta());
        }

        return p;
    }
}