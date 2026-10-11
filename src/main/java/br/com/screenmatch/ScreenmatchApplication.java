package br.com.screenmatch;

import br.com.screenmatch.service.ConsumoOmdbAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String url = System.getenv("API_URL");
		var consumoOmdbAPI = new ConsumoOmdbAPI();
		var json = consumoOmdbAPI.obterDados(url);
		System.out.println(json);

	}
}
