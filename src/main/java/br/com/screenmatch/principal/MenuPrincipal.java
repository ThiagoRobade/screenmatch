package br.com.screenmatch.principal;

import br.com.screenmatch.model.DadosEpisodio;
import br.com.screenmatch.model.DadosSerie;
import br.com.screenmatch.model.DadosTemporada;
import br.com.screenmatch.service.ConsumoOmdbAPI;
import br.com.screenmatch.service.ConversorDados;

import java.util.ArrayList;
import java.util.List;
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

        List<DadosTemporada> dadosTemporadas = new ArrayList<>();
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoOmdbAPI.obterDados(API_URL + nomeSerie.replace(" ", "+") + API_KEY + "&Season=" + i);
            DadosTemporada dadosTemporada = conversorDados.obterDados(json, DadosTemporada.class);
            dadosTemporadas.add(dadosTemporada);
        }
        dadosTemporadas.forEach(System.out::println);

        /*
        -- ESSE TRECHO FOI SUBSTITUIDO POR UMA FUNÇÃO LAMBDA A PARTIR DA LINHA 49
        for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = dadosTemporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }
         */

        dadosTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        dadosTemporadas.forEach(System.out::println);
    }

}
