package br.com.martinsluis.screenmatch.dto;

import br.com.martinsluis.screenmatch.model.Generos;

public record SerieDTO (
        Long id,
        String titulo,
        Integer totalTemporadas,
        Double avaliacao,
        Generos genero,
        String atores,
        String poster,
        String sinopse
){}
