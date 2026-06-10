package is.progetto.factory;

import is.model.*;

/*
* Design Pattern: Abstract Factory
* Crea oggetti di tipo ContenutiMultimediali e si collega alle factory concrete
* Permette l'estensibilità(nuovi tipi di media)
 */
public interface MediaFactory {
    ContenutiMultimediali crea(int id, String titolo, String regista, int annoUscita, int valutazione);

    String getTipoContenuto();

    ContenutiMultimediali creaVuoto();

}
