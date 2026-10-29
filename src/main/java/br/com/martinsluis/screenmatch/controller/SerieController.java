package br.com.martinsluis.screenmatch.controller;

import br.com.martinsluis.screenmatch.model.Serie;
import br.com.martinsluis.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SerieController {

    @Autowired
    private SerieRepository repositorio;

    @GetMapping("/series")
    public List<Serie> obterSeries(){
        return repositorio.findAll();
    }
}
