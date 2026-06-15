package is.progetto.factory;

import is.model.ContenutiMultimediali;
import is.model.Film;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilmFactoryTest {

    @Test
    void testSingletonInstance() {
        // Chiede alla Factory due istanze in momenti diversi
        FilmFactory istanza1 = FilmFactory.getInstance();
        FilmFactory istanza2 = FilmFactory.getInstance();

        // assertSame verifica che i due oggetti puntino alla STESSA area di memoria.
        // Se passa, dimostra matematicamente che il pattern Singleton funziona
        assertSame(istanza1, istanza2, "Il pattern Singleton ha fallito: le istanze in memoria sono diverse!");
    }

    @Test
    void testCreazioneTramiteFactory() {
        // Dichiaro la variabile usando l'interfaccia generale (MediaFactory),
        // ma le assegno l'istanza concreta (FilmFactory)
        MediaFactory factoryGenerica = FilmFactory.getInstance();

        // Ora uso l'interfaccia per creare il media
        ContenutiMultimediali risultato = factoryGenerica.crea(
                10, "Interstellar", "Nolan", 2014, 5
        );

        assertNotNull(risultato, "L'oggetto creato non deve essere nullo");
        assertTrue(risultato instanceof Film, "La Factory deve sfornare un oggetto di tipo Film");
        assertEquals(Film.TIPO_CONTENUTO, risultato.getTipoContenuto());
        assertEquals("Interstellar", risultato.getTitolo());
    }
}