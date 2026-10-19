package br.com.screenmatch.principal;

import br.com.screenmatch.model.DadosEpisodio;
import br.com.screenmatch.model.DadosSerie;
import br.com.screenmatch.model.DadosTemporada;
import br.com.screenmatch.model.Episodio;
import br.com.screenmatch.service.ConsumoOmdbAPI;
import br.com.screenmatch.service.ConversorDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MenuPrincipal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoOmdbAPI consumoOmdbAPI = new ConsumoOmdbAPI();
    private ConversorDados conversorDados = new ConversorDados();
    private final String API_URL = System.getenv("API_URL");
    private final String API_KEY = System.getenv("API_KEY");


    public void exibirMenu() {
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("╠══════════════════════════SCREENMATCH════════════════════════╣");
        System.out.println("║1. Digite o nome da série                                    ║");

        var nomeSerie = leitura.nextLine();
        var json = consumoOmdbAPI.obterDados(API_URL + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversorDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoOmdbAPI.obterDados(API_URL + nomeSerie.replace(" ", "+") + API_KEY + "&Season=" + i);
            DadosTemporada dadosTemporada = conversorDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        /*
        -- ESSE TRECHO FOI SUBSTITUIDO POR UMA FUNÇÃO LAMBDA A PARTIR DA LINHA 49
        for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = dadosTemporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }
         */

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//        temporadas.forEach(System.out::println);
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//
//        System.out.println("\n Top 10 episódios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(10)
//                .map(e-> e.titulo().toUpperCase())
//                .forEach(System.out::println);
//
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.println("Digite um trecho do titulo do episódio ");
//        var trechoTitulo = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Episódio encontrado: " + episodioBuscado.get());
//        } else {
//            System.out.println("Episódio não encontrado.");
//        }

//
//        System.out.println("╔═════════════════════════════════════════════════════════════╗");
//        System.out.println("╠══════════════════════════SCREENMATCH════════════════════════╣");
//        System.out.println("║A partir de que ano você deseja ver os episódios?            ║");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada:  " + e.getTemporada() +
//                                " Episódio: " + e.getTitulo() +
//                                " Data lançamento: " + e.getDataLancamento().format(formatador)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println("Avaliações por temporada: " + avaliacoesPorTemporada);

    }
}

