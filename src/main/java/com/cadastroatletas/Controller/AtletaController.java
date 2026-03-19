package com.cadastroatletas.Controller;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin(origins = "https://seu-projeto.vercel.app")
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService;

    // Cadastro de novo Atleta/Aluno
    @PostMapping
    public ResponseEntity<Atleta> cadastrar(@RequestBody Atleta atleta) {
        // Garante que novos cadastros comecem como Ativos
        atleta.setAtivo(true);

        Atleta atletaSalvo = atletaRepository.save(atleta);

        // Cria o registro de pagamento (logística financeira manual)
        pagamentoService.criarPagamento(atletaSalvo.getId());

        return ResponseEntity.ok(atletaSalvo);
    }

    // Listagem para os Gráficos (Dashboard)
    @GetMapping
    public List<Atleta> listarTodos() {
        return atletaRepository.findAll();
    }

    // Listagem por Turno (Para a lista de chamada de Abril)
    @GetMapping("/turno/{turno}")
    public List<Atleta> listarPorTurno(@PathVariable String turno) {
        return atletaRepository.findByTurnoAndAtivoTrue(turno.toUpperCase());
    }
}