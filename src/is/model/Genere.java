package is.model;

public enum Genere {
    AZIONE("Azione"),
    AVVENTURA("Avventura"),
    COMMEDIA("Commedia"),
    DRAMMA("Dramma"),
    FANTASCIENZA("Fantascienza"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    SPLATTER("Splatter"),
    ROMANTICO("Romantico"),
    THRILLER("Thriller"),
    ANIMAZIONE("Animazione"),
    DOCUMENTARIO("Documentario"),
    DRAMMATICO("Drammatico"),
    ALTRO("Altro");

    private final String nome;

    Genere(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
