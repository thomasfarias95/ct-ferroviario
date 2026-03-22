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

    /**
     * LÓGICA DE BALANCETE: Roda todo dia 1º do mês.
     * Reseta o status do atleta para PENDENTE e gera nova cobrança.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void renovarPagamentosMensais() {
        List<Atleta> atletasAtivos = atletaRepository.findAll()
                .stream()
                .filter(atleta -> atleta.getAtivo() != null && atleta.getAtivo())
                .toList();

        for (Atleta atleta : atletasAtivos) {
            atleta.setStatusPagamento("PENDENTE");
            atletaRepository.save(atleta);
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
        pagamentoRepository.save(novoPagamento);
    }

    /**
     * CONFIRMAÇÃO DE PAGAMENTO (Versão Corrigida para Persistência)
     * Busca o primeiro pagamento pendente e atualiza o status do atleta no banco.
     */
    @Transactional
    public Pagamento confirmarPagamentoPeloAtleta(Long atletaId) {
        // Buscamos o registro de pagamento pendente mais antigo/disponível
        Pagamento p = pagamentoRepository.findAll().stream()
                .filter(pag -> pag.getAtleta().getId().equals(atletaId))
                .filter(pag -> !pag.isPago())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum pagamento pendente encontrado para o ID: " + atletaId));

        // 1. Atualiza o registro do Pagamento
        p.setPago(true);
        p.setDataPagamento(LocalDate.now());

        // 2. ATUALIZAÇÃO CRUCIAL: Muda o status no Atleta para persistir no Dashboard (F5)
        Atleta atleta = p.getAtleta();
        atleta.setStatusPagamento("EM_DIA");
        atletaRepository.save(atleta);

        // 3. Salva o pagamento e retorna
        return pagamentoRepository.save(p);
    }

    public Pagamento confirmarPagamento(Long pagamentoId) {
        Pagamento p = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        p.setPago(true);
        p.setDataPagamento(LocalDate.now());

        // Garantindo que o status do atleta também mude aqui
        Atleta atleta = p.getAtleta();
        atleta.setStatusPagamento("EM_DIA");
        atletaRepository.save(atleta);

        return pagamentoRepository.save(p);
    }
}