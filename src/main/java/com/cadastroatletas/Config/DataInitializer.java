package com.cadastroatletas.Config;

import com.cadastroatletas.Entity.Professor; // Ajuste o pacote se necessário
import com.cadastroatletas.Repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String senhaCriptografada = passwordEncoder.encode("mudar123");

        // Criar Sensei Aldisio se não existir

        if (repository.findByEmail("JU020734") == null) {
            Professor aldisio = new Professor();
            aldisio.setNomeCompleto("ALDISIO SEVERINO DA SILVA");
            aldisio.setEmail("JU020734"); // Define o registro como login (no campo email)
            aldisio.setSenha(senhaCriptografada);
            aldisio.setPapel("ADMIN");
            aldisio.setNumeroContato("(81)98864-3950");
            aldisio.setGraduacao("Preta 5º DAN");
            aldisio.setNumeroZempo("JU020734"); // Mantendo consistência com o registro
            aldisio.setSexo("Masculino");
            aldisio.setIdade(56);

            repository.save(aldisio);
        }

        // Criar Thomas se não existir
        if (repository.findByEmail("JU046068") == null) {
            Professor thomas = new Professor();
            thomas.setNomeCompleto("THOMAS ANDERSON DA SILVA FARIAS");
            thomas.setEmail("JU046068");
            thomas.setSenha(senhaCriptografada);
            thomas.setPapel("ADMIN");
            thomas.setNumeroContato("(81)98763-7334");
            thomas.setGraduacao("Preta 1º DAN");
            thomas.setNumeroZempo("JU046068");
            thomas.setSexo("Masculino");
            thomas.setIdade(30);

            repository.save(thomas);
        }
    }
}
