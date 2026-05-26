package is.model;

public final class Film implements ContenutiMultimediali{
    private final String id;
    private final String titolo;
    private final String regista;
    private final int annoUscita;
    private final Genere genere;
    private final int valutazione;
    private final StatoVisione statoVisione;

    //Costruttore privato per far modo che si possa creare solo tramite il Pattern Builder
    private Film(FilmBuilder builder) {
        this.id = builder.id;
        this.titolo = builder.titolo;
        this.regista = builder.regista;
        this.annoUscita = builder.annoUscita;
        this.genere = builder.genere;
        this.valutazione = builder.valutazione;
        this.statoVisione = builder.statoVisione;
    }
    @Override
    public String getId() {return this.id;}
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

    public static class FilmBuilder {
        private String id;
        private String titolo;
        private String regista;
        private int annoUscita;
        private Genere genere;
        private int valutazione;
        private StatoVisione statoVisione;

        //Costruttore per campi obbligatori
        public FilmBuilder (String id, String titolo, String regista) {
            this.id = id;
            this.titolo = titolo;
            this.regista = regista;
        }

        //Costruttori per campi restanti
        public FilmBuilder annoUscita(int annoUscita) {
            if(annoUscita < 2026)
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

        public FilmBuilder statoVisione(StatoVisione statoVisione) {
            this.statoVisione = statoVisione;
            return this;
        }

        //Metodo per costruire l'oggetto
        public Film build() {
            return new Film(this);
        }
    }
}
