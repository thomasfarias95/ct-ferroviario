package com.cadastroatletas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CadastroAtletasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadastroAtletasApplication.class, args);
	}

}
