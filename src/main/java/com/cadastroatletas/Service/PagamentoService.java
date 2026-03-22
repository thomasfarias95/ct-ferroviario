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
        // Ajuste aqui também para isPago se necessário no Repository
        return pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(dataLimite);
    }

    public List<Pagamento> listarInadimplentes() {
        return pagamentoRepository.findByPagoFalseAndDataVencimentoBefore(LocalDate.now());
    }

    /**
     * LÓGICA DE BALANCETE: Roda todo dia 1º do mês.
     * Reseta o status do atleta na tela e gera a nova cobrança no banco.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void renovarPagamentosMensais() {
        List<Atleta> atletasAtivos = atletaRepository.findAll()
                .stream()
                .filter(atleta -> atleta.getAtivo() != null && atleta.getAtivo())
                .toList();

        for (Atleta atleta : atletasAtivos) {
            // 1. O status só volta para PENDENTE quando o mês vira aqui
            atleta.setStatusPagamento("PENDENTE");
            atletaRepository.save(atleta);

            // 2. Gera o registro de pagamento para o novo mês
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

        // Se hoje já passou do dia de vencimento, agenda para o mês seguinte
        if (vencimento.isBefore(LocalDate.now())) {
            vencimento = vencimento.plusMonths(1);
        }

        novoPagamento.setDataVencimento(vencimento);
        pagamentoRepository.save(novoPagamento);
    }

    @Transactional
    public Pagamento confirmarPagamentoPeloAtleta(Long atletaId) {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // CORREÇÃO: Usando isPago() para evitar erro de compilação do Lombok
        Pagamento p = pagamentoRepository.findAll().stream()
                .filter(pag -> pag.getAtleta().getId().equals(atletaId))
                .filter(pag -> !pag.isPago())
                .filter(pag -> !pag.getDataVencimento().isBefore(inicioMes) && !pag.getDataVencimento().isAfter(fimMes))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum pagamento pendente para este mês"));

        p.setPago(true);
        p.setDataPagamento(LocalDate.now());

        // PERSISTÊNCIA: Atualiza o status no Atleta para o Dashboard não zerar no F5
        Atleta atleta = p.getAtleta();
        atleta.setStatusPagamento("EM_DIA");
        atletaRepository.save(atleta);

        return pagamentoRepository.save(p);
    }

    // Método antigo mantido por compatibilidade
    public Pagamento confirmarPagamento(Long pagamentoId) {
        Pagamento p = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        p.setPago(true);
        p.setDataPagamento(LocalDate.now());
        return pagamentoRepository.save(p);
    }
}