package is.progetto.controller;

import is.model.ContenutiMultimediali;
import is.model.Film;
import is.model.Genere;
import is.model.StatoVisione;
import is.progetto.factory.FilmFactory;
import is.progetto.persistence.DBManager;
import is.progetto.persistence.InitDB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class MediaControllerTest {

    private MediaController facade;

    @BeforeEach
    void setUp() {
        // Svuoto il database prima di iniziare
        InitDB.pulisciDB();
        // Inizializzo il facade
        facade = new MediaController();
    }

    @AfterEach
    void tearDown() {
        // Faccio pulizia anche alla fine, lasciando il DB intatto per l'app vera
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM media");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFlussoAggiungiEAnnulla() {
        // Verifico che all'avvio la collezione sia vuota e non si possa annullare nulla
        assertEquals(0, facade.getCollezioneCompleta().size(), "Il DB dovrebbe essere vuoto");
        assertFalse(facade.puoAnnullare(), "All'avvio non si deve poter annullare nulla");

        // 1. Simulo l'utente che compila il Form
        ContenutiMultimediali nuovoFilm = FilmFactory.getInstance().crea(
                0, "Inception", "Nolan", 2010, 5
        );
        nuovoFilm.setGenere(Genere.FANTASCIENZA);
        nuovoFilm.setStatoVisione(StatoVisione.VISTO);
        if (nuovoFilm instanceof Film f) {
            f.setDurata(148);
        }

        // 2. L'utente preme "Salva" (Invoca la Facade)
        boolean esitoAggiunta = facade.aggiungiNuovoMedia(nuovoFilm);

        // Verifico l'aggiunta
        assertTrue(esitoAggiunta, "L'aggiunta deve avere successo");
        assertEquals(1, facade.getCollezioneCompleta().size(), "Ci deve essere 1 film nella lista");
        assertTrue(facade.puoAnnullare(), "Ora il bottone Annulla deve potersi accendere!");

        // 3. L'utente preme "Annulla Ultima Azione (Undo)"
        boolean esitoAnnulla = facade.annullaUltimaAzione();

        // Verifico il ripristino
        assertTrue(esitoAnnulla, "L'annullamento deve avere successo");
        assertEquals(0, facade.getCollezioneCompleta().size(), "Il film deve essere stato rimosso dal DB!");
        assertFalse(facade.puoAnnullare(), "Lo storico deve essere di nuovo vuoto");
    }

    @Test
    void testFlussoModificaTramiteFacade() {
        // 1. Aggiungo un film iniziale
        ContenutiMultimediali filmBase = FilmFactory.getInstance().crea(
                0, "Avatar", "James Cameron", 2009, 4
        );
        if(filmBase instanceof Film f) f.setDurata(162);
        facade.aggiungiNuovoMedia(filmBase);

        // Estraggo il film appena salvato per avere il suo VERO ID generato dal DB
        ContenutiMultimediali filmSalvato = facade.cercaPerTitolo("Avatar").get(0);

        // 2. L'utente modifica il film (cambia il voto a 5)
        ContenutiMultimediali filmModificato = FilmFactory.getInstance().crea(
                filmSalvato.getId(), "Avatar", "James Cameron", 2009, 5
        );
        if(filmModificato instanceof Film f) f.setDurata(162);

        // Invoco l'aggiornamento
        facade.aggiornaMedia(filmSalvato, filmModificato);

        // 3. Verifico la modifica
        ContenutiMultimediali filmDopoModifica = facade.getCollezioneCompleta().get(0);
        assertEquals(5, filmDopoModifica.getValutazione(), "La valutazione deve essere aggiornata a 5");

        // 4. Testo l'Undo della modifica
        facade.annullaUltimaAzione();

        ContenutiMultimediali filmDopoUndo = facade.getCollezioneCompleta().get(0);
        assertEquals(4, filmDopoUndo.getValutazione(), "La valutazione deve essere tornata a 4!");
    }

    @Test
    void testAggiuntaMediaDuplicato() {
        // 1. Creo un film originale
        ContenutiMultimediali filmOriginale = FilmFactory.getInstance().crea(
                0, "The Truman Show", "Peter Weir", 1998, 5
        );
        if (filmOriginale instanceof Film f) f.setDurata(103);

        // 2. Il primo inserimento deve avere successo
        boolean esitoPrimo = facade.aggiungiNuovoMedia(filmOriginale);
        assertTrue(esitoPrimo, "Il primo inserimento deve andare a buon fine");
        assertEquals(1, facade.getCollezioneCompleta().size(), "Ci deve essere 1 film nel DB");

        // 3. Creo un clone esatto (stesso titolo e regista)
        ContenutiMultimediali filmClone = FilmFactory.getInstance().crea(
                0, "The Truman Show", "Peter Weir", 1998, 5
        );
        if (filmClone instanceof Film f) f.setDurata(103);

        // 4. Il secondo inserimento DEVE fallire ed essere bloccato
        boolean esitoSecondo = facade.aggiungiNuovoMedia(filmClone);
        assertFalse(esitoSecondo, "Il sistema deve bloccare l'inserimento del clone restituendo false");

        // 5. Verifico che il DB non sia stato inquinato
        assertEquals(1, facade.getCollezioneCompleta().size(), "Il DB deve contenere ancora solo 1 film, il clone non deve essere salvato");
    }
}