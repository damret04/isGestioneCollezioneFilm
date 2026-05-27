package is.progetto.dao;

import is.model.*;

import java.util.List;

public interface MediaDao {
    void salva(ContenutiMultimediali contenutiMultimediali);

    List<ContenutiMultimediali> getTutti();

    List<ContenutiMultimediali> trovaPerId(String id);

    void aggiorna(ContenutiMultimediali contenutiMultimediali);

    void elimina(String id);

    //filtri per ricerca
    List<ContenutiMultimediali> filtraPerTitolo(String titolo);
    List<ContenutiMultimediali> filtraPerRegista(String regista);
    List<ContenutiMultimediali> filtraPerGenere(Genere genere);
    List<ContenutiMultimediali> filtraPerStatoVisione(StatoVisione statoVisione);
}
