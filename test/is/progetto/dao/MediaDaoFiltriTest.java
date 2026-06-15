package is.progetto.dao;

import is.model.ContenutiMultimediali;
import is.model.Film;
import is.model.Genere;
import is.model.StatoVisione;
import is.progetto.factory.FilmFactory;
import is.progetto.persistence.InitDB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MediaDaoFiltriTest {

    private MediaDao mediaDao;
    private List<Integer> idInseriti; // Lista per ricordarmi quali ID eliminare alla fine

    @BeforeEach
    void setUp() {
        InitDB.pulisciDB();
        mediaDao = new MediaDaoImpl();
        idInseriti = new ArrayList<>();

        // Inserisco 3 film con caratteristiche specifiche nel database
        inserisciFilmDiTest("Matrix", "Wachowski", 1999, Genere.FANTASCIENZA, 5);
        inserisciFilmDiTest("Il Padrino", "Coppola", 1972, Genere.AZIONE, 5);
        inserisciFilmDiTest("Interstellar", "Nolan", 2014, Genere.FANTASCIENZA, 4);
    }

    // Metodo helper privato per non duplicare il codice di inserimento
    private void inserisciFilmDiTest(String titolo, String regista, int anno, Genere genere, int voto) {
        ContenutiMultimediali film = FilmFactory.getInstance().crea(0, titolo, regista, anno, voto);
        film.setGenere(genere);
        film.setStatoVisione(StatoVisione.VISTO);
        if (film instanceof Film f) {
            f.setDurata(120);
        }
        mediaDao.salva(film);
        idInseriti.add(mediaDao.getUltimoIdInserito()); // Salviamo l'ID per eliminarlo dopo
    }

    @AfterEach
    void tearDown() {
        // Pulizia totale: elimina tutti i film che ho inserito in questo test
        /* for (int id : idInseriti) {
            mediaDao.elimina(id);
        }

         */
        InitDB.pulisciDB();
    }

    @Test
    void testFiltraPerGenere() {
        // Chiedo al database di dare TUTTI i film di Fantascienza
        List<ContenutiMultimediali> fantascienza = mediaDao.filtraPerGenere(Genere.FANTASCIENZA);

        // Conta quanti dei film inseriti sono tornati indietro
        long trovati = fantascienza.stream()
                .filter(f -> idInseriti.contains(f.getId()))
                .count();

        // Dovrebbero essere esattamente 2 (Matrix e Interstellar), mentre Il Padrino deve essere escluso
        assertEquals(2, trovati, "Il filtro per genere dovrebbe restituire esattamente 2 film di fantascienza");
    }

    @Test
    void testFiltraPerTitoloParziale() {
        // Ricerca testuale parziale (ignorando maiuscole/minuscole)
        List<ContenutiMultimediali> risultati = mediaDao.filtraPerTitolo("padri");

        boolean trovato = risultati.stream().anyMatch(f -> f.getTitolo().equals("Il Padrino"));
        assertTrue(trovato, "La ricerca parziale 'padri' deve trovare 'Il Padrino'");
    }

    @Test
    void testOrdinamentoPerAnnoDecrescente() {
        // Chiedo i film ordinati dal più recente (2014) al più vecchio (1972)
        List<ContenutiMultimediali> ordinati = mediaDao.getTuttiOrdinatiPerAnno(false);

        // Isolo solo i film che ho inserito io (perché il DB potrebbe averne altri)
        List<ContenutiMultimediali> nostriFilm = ordinati.stream()
                .filter(f -> idInseriti.contains(f.getId()))
                .toList();

        // Verifico che l'ordine sia esattamente quello aspettato
        assertEquals("Interstellar", nostriFilm.get(0).getTitolo(), "Il primo deve essere il più recente (2014)");
        assertEquals("Matrix", nostriFilm.get(1).getTitolo(), "Il secondo deve essere quello di mezzo (1999)");
        assertEquals("Il Padrino", nostriFilm.get(2).getTitolo(), "Il terzo deve essere il più vecchio (1972)");
    }
}