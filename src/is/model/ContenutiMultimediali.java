package is.model;

public interface ContenutiMultimediali {
    int getId();
    String getTitolo();
    String getRegista();
    int getAnnoUscita();
    int getValutazione();
    Genere getGenere();
    StatoVisione getStatoVisione();


    boolean equals(Object obj);
    int hashCode();
    String toString();

}
