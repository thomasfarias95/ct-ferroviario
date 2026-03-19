package com.cadastroatletas.Service;



import com.cadastroatletas.Entity.Atleta;
import com.cadastroatletas.Repository.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private AtletaRepository atletaRepository;

    @Scheduled(cron = "0 0 9 * * *") // Roda às 09:00 todos os dias
    public void rotinaDeCobrancaAutomatica() {
        LocalDate hoje = LocalDate.now();

        // TRAVA: Só envia se estivermos em MAIO de 2026 ou depois
        if (hoje.getYear() < 2026 || (hoje.getYear() == 2026 && hoje.getMonthValue() < 5)) {
            System.out.println("Logística: Aguardando Maio de 2026 para iniciar disparos automáticos.");
            return;
        }

        // Busca atletas que vencem em 3 dias
        List<Atleta> paraNotificar = atletaRepository.findAtletasParaNotificar(3);

        for (Atleta atleta : paraNotificar) {
            try {
                enviarLogicaWhatsapp(atleta);

                // Marca que já avisou hoje para não repetir
                atleta.setUltimaNotificacao(hoje);
                atletaRepository.save(atleta);
            } catch (Exception e) {
                System.err.println("Erro ao processar notificação do atleta: " + atleta.getNomeCompleto());
            }
        }
    }

    private void enviarLogicaWhatsapp(Atleta atleta) {
        String destinatario = (atleta.getNomeResponsavel() != null && !atleta.getNomeResponsavel().isEmpty())
                ? atleta.getNomeResponsavel()
                : atleta.getNome();

        String mensagem = String.format(
                "Olá, %s! 🥋\n\n" +
                        "Passando para lembrar que a mensalidade do(a) judoca *%s* no *CT Ferroviário* vence em 3 dias (dia %d).\n\n" +
                        "Para sua comodidade, você pode realizar o pagamento via PIX.\n" +
                        "🔑 Chave PIX: `titojudo07@gmail.com`\n\n" +
                        "Após o pagamento, o status será atualizado no sistema. Oss!",
                destinatario,
                atleta.getNomeCompleto(),
                atleta.getDiaVencimento()
        );

        // Por enquanto, apenas imprime no console do Render.
        // Em maio, conectamos o disparador real.
        System.out.println("--- NOTIFICAÇÃO GERADA ---");
        System.out.println("Para: " + atleta.getTelefone());
        System.out.println("Mensagem: " + mensagem);
    }
}
