package com.cadastroatletas.Controller;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin(origins = "http://localhost:3000")
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PagamentoService pagamentoService;

    // Mantenha APENAS este método POST
    @PostMapping
    public ResponseEntity<Atleta> cadastrar(@RequestBody Atleta atleta) {
        // 1. Salva o atleta no banco
        Atleta atletaSalvo = atletaRepository.save(atleta);

        // 2. Cria o pagamento automático usando o Service
        pagamentoService.criarPagamento(atletaSalvo.getId());

        return ResponseEntity.ok(atletaSalvo);
    }
}