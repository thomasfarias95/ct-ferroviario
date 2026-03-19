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

    // Listar quem está com o boleto vencendo em X dias
    public List<Pagamento> listarVencimentosProximos(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(dataLimite);
    }

    // Listar quem já passou do vencimento e não pagou (Inadimplentes)
    public List<Pagamento> listarInadimplentes() {
        return pagamentoRepository.findByPagoFalseAndDataVencimentoBefore(LocalDate.now());
    }

    // AUTOMAÇÃO: Gera uma NOVA linha na tabela para cada aluno ativo no dia 1º
    @Scheduled(cron = "0 0 0 1 * ?")
    public void renovarPagamentosMensais() {
        // Buscamos os atletas (quem gera a mensalidade é o Atleta Ativo, não o pagamento antigo)
        List<Atleta> atletasAtivos = atletaRepository.findAll()
                .stream()
                .filter(Atleta::isAtivo) // Supondo que adicionamos o campo 'ativo' na Entity
                .toList();

        for (Atleta atleta : atletasAtivos) {
            gerarNovoPagamento(atleta);
        }
    }

    // Criar o primeiro pagamento ao cadastrar um novo atleta
    public void criarPagamento(Long atletaId) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        gerarNovoPagamento(atleta);
    }

    // Método auxiliar para centralizar a regra de negócio
    private void gerarNovoPagamento(Atleta atleta) {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setAtleta(atleta);
        novoPagamento.setValor(new BigDecimal("100.00")); // Valor padrão do CT
        novoPagamento.setPago(false);

        // Define o vencimento com base no dia escolhido (ex: dia 10 de cada mês)
        LocalDate vencimento = LocalDate.now().withDayOfMonth(atleta.getDiaVencimento());

        // Se hoje já passou do dia de vencimento, coloca para o mês que vem
        if (vencimento.isBefore(LocalDate.now())) {
            vencimento = vencimento.plusMonths(1);
        }

        novoPagamento.setDataVencimento(vencimento);
        pagamentoRepository.save(novoPagamento);
    }

    // MÉTODO PARA O BOTÃO DE "DAR BAIXA" (O que você vai usar no Front)
    public Pagamento confirmarPagamento(Long pagamentoId) {
        Pagamento p = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        p.setPago(true);
        p.setDataPagamento(LocalDate.now()); // Registra o dia que o dinheiro entrou
        return pagamentoRepository.save(p);
    }
}