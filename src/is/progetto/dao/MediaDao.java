package is.progetto.dao;

import is.model.*;

import java.util.List;
import java.util.Optional;

public interface MediaDao {
    void salva(ContenutiMultimediali contenutiMultimediali);

    List<ContenutiMultimediali> getTutti();

    Optional<ContenutiMultimediali> trovaPerId(int id);

    void aggiorna(ContenutiMultimediali contenutiMultimediali);

    void elimina(int id);

    // filtri per ricerca
    List<ContenutiMultimediali> filtraPerTitolo(String titolo);
    List<ContenutiMultimediali> filtraPerRegista(String regista);
    List<ContenutiMultimediali> filtraPerGenere(Genere genere);
    List<ContenutiMultimediali> filtraPerStatoVisione(StatoVisione statoVisione);
}
