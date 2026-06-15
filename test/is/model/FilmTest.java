package is.model;

import is.progetto.factory.FilmFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    @Test
    void testCreazioneFilmCorretta() {
        ContenutiMultimediali film = FilmFactory.getInstance().crea(
                1, "Il Signore degli Anelli", "Peter Jackson", 2001, 5
        );
        film.setGenere(Genere.FANTASY);
        film.setStatoVisione(StatoVisione.VISTO);

        if(film instanceof Film f) {
            f.setDurata(178);
            assertEquals(178, f.getDurata());
        }

        assertEquals("Il Signore degli Anelli", film.getTitolo());
        assertEquals("Peter Jackson", film.getRegista());
        assertEquals(2001, film.getAnnoUscita());
        assertEquals(5, film.getValutazione());
        assertEquals(Genere.FANTASY, film.getGenere());
    }

    @Test
    void testEccezioneAnnoUscitaNonValido() {
        IllegalArgumentException eccezione = assertThrows(IllegalArgumentException.class, () -> {
            Film.builder().annoUscita(2030).build();
        });
        assertEquals("Anno di uscita non valido.", eccezione.getMessage());
    }

    @Test
    void testEccezioneValutazioneTroppoAlta() {
        IllegalArgumentException eccezione = assertThrows(IllegalArgumentException.class, () -> {
            Film.builder().valutazione(6).build();
        });
        assertEquals("Valutazione deve essere compresa tra 1 e 5.", eccezione.getMessage());
    }

    @Test
    void testEccezioneValutazioneTroppoBassa() {
        IllegalArgumentException eccezione = assertThrows(IllegalArgumentException.class, () -> {
            Film.builder().valutazione(0).build();
        });
        assertEquals("Valutazione deve essere compresa tra 1 e 5.", eccezione.getMessage());
    }

    @Test
    void testEccezioneDurataNegativa() {
        IllegalArgumentException eccezione = assertThrows(IllegalArgumentException.class, () -> {
            Film.builder().durata(-10).build();
        });
        assertEquals("La durata deve essere maggiore di 0 min.", eccezione.getMessage());
    }
}