package is.model;

public interface ContenutiMultimediali {
    int getId();
    String getTitolo();
    String getRegista();
    int getAnnoUscita();
    int getValutazione();
    Genere getGenere();
    StatoVisione getStatoVisione();

    String getTipoContenuto();

    void setTitolo(String titolo);
    void setRegista(String regista);
    void setAnnoUscita(int annoUscita);
    void setValutazione(int valutazione);
    void setGenere(Genere genere);
    void setStatoVisione(StatoVisione statoVisione);


    boolean equals(Object obj);
    int hashCode();
    String toString();

}
