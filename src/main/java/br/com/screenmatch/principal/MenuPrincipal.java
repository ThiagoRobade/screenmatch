package br.com.screenmatch.principal;

import br.com.screenmatch.model.DadosSerie;
import br.com.screenmatch.service.ConsumoOmdbAPI;
import br.com.screenmatch.service.ConversorDados;

import java.util.Scanner;

public class MenuPrincipal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoOmdbAPI consumoOmdbAPI = new ConsumoOmdbAPI();
    private ConversorDados conversorDados = new ConversorDados();
    private final String API_URL = System.getenv("API_URL");
    private final String API_KEY = System.getenv("API_KEY");


    public void exibirMenu() {
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("╠═════════════SCREENMATCH═══════════╣");
        System.out.println("║1. Digite o nome da série          ║");
        var nomeSerie = leitura.nextLine();
        var json = consumoOmdbAPI.obterDados(API_URL + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversorDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);
    }

}
