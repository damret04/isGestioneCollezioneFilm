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
    public ContenutiMultimediali crea(String id, String titolo, String regista, int annoUscita, int valutazione) {
        return new Film.FilmBuilder()
                .id(id)
                .titolo(titolo)
                .regista(regista)
                .annoUscita(annoUscita)
                .valutazione(valutazione)
                .build();
    }
}
