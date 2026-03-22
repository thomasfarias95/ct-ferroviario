package com.cadastroatletas.Controller;

import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import com.cadastroatletas.Service.AtletaService;
import com.cadastroatletas.Service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cadastro/atletas")
@CrossOrigin(origins = "*")
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

    @GetMapping
    public List<Atleta> listarTodos() {
        return atletaRepository.findByAtivoTrue();
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

    // --- DOWNLOAD DE PDF (AGORA COM CONTEÚDO REAL) ---
    @GetMapping("/{id}/relatorio-pdf")
    public ResponseEntity<byte[]> baixarRelatorioAtleta(@PathVariable Long id) {
        // Agora chamamos o método que criamos no Service acima
        byte[] pdfBytes = atletaService.gerarRelatorioPdf(id);

        String nomeArquivo = "atleta_" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}