package br.com.martinsluis.screenmatch.principal;

import br.com.martinsluis.screenmatch.model.Episodio;
import br.com.martinsluis.screenmatch.model.Generos;
import br.com.martinsluis.screenmatch.model.Serie;
import br.com.martinsluis.screenmatch.model.dto.DadosSerieDTO;
import br.com.martinsluis.screenmatch.model.dto.DadosTemporadaDTO;
import br.com.martinsluis.screenmatch.repository.SerieRepository;
import br.com.martinsluis.screenmatch.service.ConsultaGemini;
import br.com.martinsluis.screenmatch.service.ConsumoAPI;
import org.springframework.stereotype.Component;
import br.com.martinsluis.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=11491604";
    List<DadosSerieDTO> dadosSeries = new ArrayList<>();

    private final SerieRepository serieRepository;
    private final ConsultaGemini consultaGemini;

    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository serieRepository, ConsultaGemini consultaGemini) {
        this.serieRepository = serieRepository;
        this.consultaGemini = consultaGemini;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao!=0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar série por ator
                    6 - Top 5 Séries
                    7 - Buscar séries por gênero
                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSerieBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorGenero();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void listarSerieBuscadas() {
        series = serieRepository.findAll();
        series.stream()
                        .sorted(Comparator.comparing(Serie::getGenero))
                        .forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        DadosSerieDTO dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        serieRepository.save(serie);
        System.out.println(dados);
    }

    private DadosSerieDTO getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerieDTO dados = conversor.obterDados(json, DadosSerieDTO.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSerieBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporadaDTO> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporadaDTO dadosTemporada = conversor.obterDados(json, DadosTemporadaDTO.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(dadosTemporadaDTO -> dadosTemporadaDTO.episodios().stream()
                            .map(e -> new Episodio(dadosTemporadaDTO.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorTitulo() {

        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }


    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome para a busca:");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de qual nota? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> serieEncontradas = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " tabalhou");
        serieEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorGenero() {
        System.out.println("Deseja buscar séries de qual gênero?");
        var nomGenero = leitura.nextLine();
        Generos genero = Generos.fromPortugues(nomGenero);
        List<Serie> seriesPorGenero = serieRepository.findByGenero(genero);
        System.out.println("Séries do gênero: " + nomGenero);
        seriesPorGenero.forEach(System.out::println);
    }
}