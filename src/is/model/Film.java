package is.model;

import java.util.Objects;

public final class Film implements ContenutiMultimediali{
    public static final String TIPO_CONTENUTO = "FILM";

    private int id;
    private String titolo;
    private String regista;
    private int annoUscita;
    private Genere genere;
    private int valutazione;
    private int durata; //durata in minuti
    private StatoVisione statoVisione;

    //Costruttore privato per far modo che si possa creare solo tramite il Pattern Builder
    private Film(FilmBuilder builder) {
        this.id = builder.id;
        this.titolo = builder.titolo;
        this.regista = builder.regista;
        this.annoUscita = builder.annoUscita;
        this.genere = builder.genere;
        this.valutazione = builder.valutazione;
        this.durata = builder.durata;
        this.statoVisione = builder.statoVisione;
    }
    @Override
    public int getId() {return this.id;}
    @Override
    public String getTitolo() {return this.titolo;}
    @Override
    public String getRegista() {return this.regista;}
    @Override
    public int getAnnoUscita() {return this.annoUscita;}
    @Override
    public Genere getGenere() {return this.genere;}
    @Override
    public int getValutazione() {return this.valutazione;}
    @Override
    public StatoVisione getStatoVisione() {return this.statoVisione;}

    @Override
    public String getTipoContenuto() {return TIPO_CONTENUTO;}

    public int getDurata() {return this.durata;}

    @Override
    public void setTitolo(String titolo) {this.titolo = titolo;}
    @Override
    public void setRegista(String regista) {this.regista = regista;}
    @Override
    public void setAnnoUscita(int annoUscita) {this.annoUscita = annoUscita;}
    @Override
    public void setValutazione(int valutazione) {this.valutazione = valutazione;}
    @Override
    public void setGenere(Genere genere) {this.genere = genere;}
    @Override
    public void setStatoVisione(StatoVisione statoVisione) {this.statoVisione = statoVisione;}
    public void setDurata(int durata) {this.durata = durata;}


    public static FilmBuilder builder() {
        return new FilmBuilder();
    }

    public static class FilmBuilder {
        private int id;
        private String titolo;
        private String regista;
        private int annoUscita;
        private Genere genere;
        private int valutazione;
        private int durata;
        private StatoVisione statoVisione;


        public FilmBuilder id(int id){
            this.id = id;
            return this;
        }

        public FilmBuilder titolo(String titolo) {
            this.titolo = titolo;
            return this;
        }

        public FilmBuilder regista(String regista) {
            this.regista = regista;
            return this;
        }

        //Costruttori per campi restanti
        public FilmBuilder annoUscita(int annoUscita) {
            if(annoUscita > 2026)
                throw new IllegalArgumentException("Anno di uscita non valido.");
            this.annoUscita = annoUscita;
            return this;
        }

        public FilmBuilder genere(Genere genere) {
            this.genere = genere;
            return this;
        }

        public FilmBuilder valutazione(int valutazione) {
            if(valutazione < 1 || valutazione > 5)
                throw new IllegalArgumentException("Valutazione deve essere compresa tra 1 e 5.");
            this.valutazione = valutazione;
            return this;
        }

        public FilmBuilder durata(int durata) {
            if(durata <= 0)
                throw new IllegalArgumentException("La durata deve essere maggiore di 0 min.");
            this.durata = durata;
            return this;
        }

        public FilmBuilder statoVisione(StatoVisione statoVisione) {
            this.statoVisione = statoVisione;
            return this;
        }

        //Metodo per costruire l'oggetto
        public Film build() {
            return new Film(this);
        }
    }



    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj.getClass() != this.getClass()) return false;
        Film film = (Film) obj;
        return Objects.equals(this.id, film.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Film{" + "id=" + id + ", titolo=" + titolo + ", regista=" + regista + ", annoUscita=" + annoUscita +
                ", genere=" + genere + ", valutazione=" + valutazione + ", durata=" + durata + " min" + ", statoVisione=" +
                statoVisione.getDescrizione() + '}';
    }
}
