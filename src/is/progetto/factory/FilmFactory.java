package is.progetto.factory;

import is.model.*;
public class FilmFactory implements MediaFactory{
    private static FilmFactory instance;
    private FilmFactory() {
    }

    public static FilmFactory getInstance() {
        if (instance == null) {
            //così può funzionare anche in ambienti multithread
            synchronized (FilmFactory.class) {
                if (instance == null) {
                    instance = new FilmFactory();
                }
            }
        }
        return instance;
    }


    @Override
    public ContenutiMultimediali crea(int id, String titolo, String regista, int annoUscita, int valutazione) {
        return new Film.FilmBuilder()
                .id(id)
                .titolo(titolo)
                .regista(regista)
                .annoUscita(annoUscita)
                .valutazione(valutazione)
                .build();
    }

    //creazione di un film vuoto per il form di inserimento
    @Override
    public Film creaVuoto(){
        return new Film.FilmBuilder().build();
    }

    @Override
    public String getTipoContenuto() {
        return Film.TIPO_CONTENUTO;
    }
}
