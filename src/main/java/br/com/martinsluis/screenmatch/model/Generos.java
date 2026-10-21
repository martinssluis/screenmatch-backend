package br.com.martinsluis.screenmatch.model;

public enum Generos {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private String generoOmdb;
    private String generoPortugues;

    Generos(String generoOmdb, String generoPortugues)
    {
        this.generoOmdb = generoOmdb;
        this.generoPortugues = generoPortugues;
    }

    public static Generos fromString(String text){
        for (Generos genero : Generos.values()){
            if(genero.generoOmdb.equalsIgnoreCase(text)){
                return genero;
            }
        }
        throw new IllegalArgumentException("Nenhum gênero encontradao para a string fornecida");
    }

    public static Generos fromPortugues(String text){
        for (Generos genero : Generos.values()){
            if(genero.generoPortugues.equalsIgnoreCase(text)){
                return genero;
            }
        }
        throw new IllegalArgumentException("Nenhum gênero encontradao para a string fornecida");
    }
}
