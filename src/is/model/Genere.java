package is.model;

public enum Genere {
    AZIONE("Azione"),
    AVVENTURA("Avventura"),
    COMMEDIA("Commedia"),
    DRAMMA("Dramma"),
    FANTASCIENZA("Fantascienza"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    ROMANTICO("Romantico"),
    THRILLER("Thriller"),
    ANIMAZIONE("Animazione"),
    DOCUMENTARIO("Documentario"),
    ALTRO("Altro");

    private String nome;

    private Genere(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
