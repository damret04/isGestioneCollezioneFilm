package is.model;

public enum StatoVisione {
    VISTO("Visto"),
    DA_VEDERE("Da vedere"),
    IN_VISIONE("In visione");

    private final String descrizione;

    StatoVisione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return this.descrizione;
    }
}
