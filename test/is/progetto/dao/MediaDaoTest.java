package is.progetto.dao;

import is.model.ContenutiMultimediali;
import is.model.Film;
import is.model.Genere;
import is.model.StatoVisione;
import is.progetto.factory.FilmFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MediaDaoTest {

    private MediaDao mediaDao;
    private int idMediaDiTest = -1; // Traccia l'ID del record per la pulizia automatica

    @BeforeEach
    void setUp() {
        // Inizializza il DAO prima di ogni singolo test
        mediaDao = new MediaDaoImpl();
    }

    @AfterEach
    void tearDown() {
        // PULIZIA: Elimina la sporcizia lasciata nel database dal test
        if (idMediaDiTest != -1) {
            mediaDao.elimina(idMediaDiTest);
            idMediaDiTest = -1;
        }
    }

    @Test
    void testSalvaETrovaPerId() {
        // 1. Creazione usando la Factory
        ContenutiMultimediali nuovoMedia = FilmFactory.getInstance().crea(
                0, "Test Titolo Salva", "Regista Test", 2024, 4
        );
        nuovoMedia.setGenere(Genere.FANTASCIENZA);
        nuovoMedia.setStatoVisione(StatoVisione.DA_VEDERE);
        if (nuovoMedia instanceof Film f) {
            f.setDurata(150);
        }

        // 2. Salvataggio nel Database
        mediaDao.salva(nuovoMedia);

        // 3. Recupero l'ID inserito (visto che salva restituisce void)
        idMediaDiTest = mediaDao.getUltimoIdInserito();
        assertTrue(idMediaDiTest > 0, "L'ID generato deve essere maggiore di 0");

        // 4. Ricerca nel Database
        Optional<ContenutiMultimediali> trovato = mediaDao.trovaPerId(idMediaDiTest);

        // 5. Verifiche
        assertTrue(trovato.isPresent(), "Il media salvato deve essere presente nel database");
        assertEquals("Test Titolo Salva", trovato.get().getTitolo());
        assertEquals("Regista Test", trovato.get().getRegista());
        assertEquals(Genere.FANTASCIENZA, trovato.get().getGenere());

        // Verifica ESTENSIBILITÀ: Controllo che il tipo contenuto sia "Film"
        assertEquals(Film.TIPO_CONTENUTO, trovato.get().getTipoContenuto());
    }

    @Test
    void testAggiornaMedia() {
        // 1. Crea il record base
        ContenutiMultimediali mediaBase = FilmFactory.getInstance().crea(
                0, "Vecchio Titolo", "Vecchio Regista", 2000, 2
        );
        mediaDao.salva(mediaBase);
        idMediaDiTest = mediaDao.getUltimoIdInserito();

        // 2. Crea l'oggetto aggiornato (passando l'ID corretto)
        ContenutiMultimediali mediaModificato = FilmFactory.getInstance().crea(
                idMediaDiTest, "Nuovo Titolo", "Nuovo Regista", 2025, 5
        );
        mediaModificato.setGenere(Genere.AZIONE);
        mediaModificato.setStatoVisione(StatoVisione.VISTO);
        if (mediaModificato instanceof Film f) {
            f.setDurata(180);
        }

        // 3. Eseguo l'aggiornamento
        mediaDao.aggiorna(mediaModificato);

        // 4. Verifico i cambiamenti nel DB
        Optional<ContenutiMultimediali> dopoAggiornamento = mediaDao.trovaPerId(idMediaDiTest);
        assertTrue(dopoAggiornamento.isPresent());
        assertEquals("Nuovo Titolo", dopoAggiornamento.get().getTitolo());
        assertEquals(5, dopoAggiornamento.get().getValutazione());
        assertEquals(StatoVisione.VISTO, dopoAggiornamento.get().getStatoVisione());

        if (dopoAggiornamento.get() instanceof Film f) {
            assertEquals(180, f.getDurata());
        }
    }

    @Test
    void testEliminaMedia() {
        // 1. Inserisco il record da sacrificare
        ContenutiMultimediali daEliminare = FilmFactory.getInstance().crea(
                0, "Media da Eliminare", "Regista", 2000, 3
        );

        // Dò una durata valida al film di test
        if (daEliminare instanceof Film f) {
            f.setDurata(120);
        }

        mediaDao.salva(daEliminare);
        int idDaEliminare = mediaDao.getUltimoIdInserito();

        // Verifico che esista prima dell'eliminazione
        assertTrue(mediaDao.trovaPerId(idDaEliminare).isPresent());

        // 2. Eseguo l'eliminazione
        mediaDao.elimina(idDaEliminare);

        // 3. Verifico che sia sparito nel nulla
        Optional<ContenutiMultimediali> risultatoVuoto = mediaDao.trovaPerId(idDaEliminare);
        assertFalse(risultatoVuoto.isPresent(), "Il media non dovrebbe più esistere nel database");
    }
}