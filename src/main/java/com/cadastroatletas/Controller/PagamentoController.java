package com.cadastroatletas.Controller;

import com.cadastroatletas.Entity.Pagamento;
import com.cadastroatletas.Repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "https://seu-projeto.vercel.app")
public class PagamentoController {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @GetMapping("/atleta/{atletaId}")
    public ResponseEntity<List<Pagamento>> listarPorAtleta(@PathVariable Long atletaId) {
        return ResponseEntity.ok(pagamentoRepository.findByAtletaId(atletaId));
    }

    @PostMapping("/baixa")
    public ResponseEntity<Pagamento> darBaixa(@RequestBody Pagamento pagamento) {
        return ResponseEntity.ok(pagamentoRepository.save(pagamento));
    }

    // Novo: Endpoint para listar quem vence nos próximos X dias
    @GetMapping("/vencimentos-proximos")
    public ResponseEntity<List<Pagamento>> listarVencimentos(@RequestParam(defaultValue = "5") int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        List<Pagamento> pendentes = pagamentoRepository.findByPagoFalseAndDataVencimentoLessThanEqual(dataLimite);
        return ResponseEntity.ok(pendentes);
    }
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<Pagamento> confirmarPagamento(@PathVariable Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        pagamento.setPago(true); // Muda o status
        pagamento.setDataPagamento(LocalDate.now()); // Registra o dia da baixa

        pagamentoRepository.save(pagamento);
        return ResponseEntity.ok(pagamento);
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> listarTodos() {
        List<Pagamento> lista = pagamentoRepository.findAll();
        return ResponseEntity.ok(lista);
    }
}
