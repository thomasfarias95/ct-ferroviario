package com.cadastroatletas.Controller;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Service.AtletaService;
import com.cadastroatletas.Service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
// Ajuste de CORS para permitir que o celular identifique o download
@CrossOrigin(origins = "*", exposedHeaders = "Content-Disposition")
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private AtletaService atletaService;

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<Atleta> cadastrar(@RequestBody Atleta atleta) {
        atleta.setAtivo(true);
        Atleta atletaSalvo = atletaRepository.save(atleta);
        pagamentoService.criarPagamento(atletaSalvo.getId());
        return ResponseEntity.ok(atletaSalvo);
    }

    // ALTERAÇÃO: Listar todos (Ativos e Inativos) para o Dashboard permitir a reativação
    @GetMapping
    public List<Atleta> listarTodos() {
        return atletaRepository.findAll();
    }

    @GetMapping("/turno/{turno}")
    public List<Atleta> listarPorTurno(@PathVariable String turno) {
        return atletaRepository.findByTurnoAndAtivoTrue(turno.toUpperCase());
    }

    @PatchMapping("/{id}/graduacao")
    public ResponseEntity<Atleta> promover(@PathVariable Long id, @RequestBody String novaGraduacao) {
        String graduacaoLimpa = novaGraduacao.replace("\"", "");
        Atleta atualizado = atletaService.promoverAtleta(id, graduacaoLimpa);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Atleta> alterarStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        Atleta atualizado = atletaService.alterarStatusAtivo(id, ativo);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/{id}/relatorio-pdf")
    public ResponseEntity<byte[]> baixarRelatorioAtleta(@PathVariable Long id) {
        byte[] pdfBytes = atletaService.gerarRelatorioPdf(id);

        // Nome do arquivo sanitizado para evitar problemas em navegadores mobile
        String nomeArquivo = "Relatorio_Atleta_" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> darBaixaPagamento(@PathVariable Long id) {
        try {
            // Chama o service que criamos acima
            pagamentoService.confirmarPagamentoPeloAtleta(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}