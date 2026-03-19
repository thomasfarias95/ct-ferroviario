package com.cadastroatletas.Controller;

import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "https://seu-projeto.vercel.app")
public class DashboardController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @GetMapping("/estatisticas")
    public Map<String, Object> getEstatisticasGerais() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Dados para o Gráfico de Pizza: Gênero
        // Retorna algo como: { "M": 15, "F": 10 }
        stats.put("genero", atletaRepository.findAll().stream()
                .filter(a -> a.getSexo() != null)
                .collect(Collectors.groupingBy(a -> a.getSexo(), Collectors.counting())));

        // 2. Dados para o Gráfico de Pizza: Faixa Etária (Logística Pedagógica)
        // Agrupa por: Infantil (até 12), Juvenil (13-17), Adulto (18+)
        stats.put("faixasEtarias", calcularFaixasEtarias());

        // 3. Dados para o Gráfico de Colunas: Financeiro do Mês Atual
        // Retorna: { "pagos": 20, "pendentes": 5 }
        stats.put("financeiroMensal", calcularFinanceiroMes());

        return stats;
    }

    private Map<String, Long> calcularFaixasEtarias() {
        List<com.cadastroatletas.Entity.Atleta> atletas = atletaRepository.findAll();
        int anoAtual = java.time.LocalDate.now().getYear();

        Map<String, Long> faixas = new HashMap<>();
        faixas.put("Infantil", atletas.stream().filter(a -> (anoAtual - a.getDataNascimento().getYear()) <= 12).count());
        faixas.put("Juvenil", atletas.stream().filter(a -> {
            int idade = anoAtual - a.getDataNascimento().getYear();
            return idade > 12 && idade <= 17;
        }).count());
        faixas.put("Adulto", atletas.stream().filter(a -> (anoAtual - a.getDataNascimento().getYear()) >= 18).count());

        return faixas;
    }

    private Map<String, Long> calcularFinanceiroMes() {
        Map<String, Long> fin = new HashMap<>();
        List<Pagamento> pagamentos = pagamentoRepository.findAll();

        // Filtra apenas os pagamentos do mês atual para o gráfico de colunas
        int mesAtual = java.time.LocalDate.now().getMonthValue();

        fin.put("Pagos", pagamentos.stream()
                .filter(p -> p.getDataVencimento().getMonthValue() == mesAtual && p.isPago()).count());
        fin.put("Pendentes", pagamentos.stream()
                .filter(p -> p.getDataVencimento().getMonthValue() == mesAtual && !p.isPago()).count());

        return fin;
    }
}
