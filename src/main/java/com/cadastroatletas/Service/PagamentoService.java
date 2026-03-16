package com.cadastroatletas.Service;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
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

    @Scheduled(cron = "0 0 0 1 * ?") // Roda às 00:00 do dia 1º de cada mês
    public void renovarPagamentosMensais() {
        // 1. Busca todos os atletas ativos ou pagamentos do mês anterior
        List<Pagamento> pagamentos = pagamentoRepository.findAll();

        for (Pagamento p : pagamentos) {
            // 2. Cria uma nova entrada para o próximo mês ou reseta o atual
            // Isso depende de como você modelou o banco (se salva histórico ou apenas atualiza)
            p.setPago(false);
            p.setDataVencimento(p.getDataVencimento().plusMonths(1));
            pagamentoRepository.save(p);
        }
    }
    public void criarPagamento(Long atletaId) {
        // 1. Busca o atleta no banco para garantir que ele existe e obter o objeto completo
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        // 2. Cria a nova instância de Pagamento
        Pagamento novoPagamento = new Pagamento();

        // 3. Faz a ligação da Chave Estrangeira (Associa o objeto Atleta ao Pagamento)
        novoPagamento.setAtleta(atleta);

        // 4. Define as regras de negócio (Vencimento para o mês seguinte e status pendente)
        novoPagamento.setDataVencimento(LocalDate.now().plusMonths(1));
        novoPagamento.setPago(false);

        // 5. Salva no banco de dados
        pagamentoRepository.save(novoPagamento);
    }

}
