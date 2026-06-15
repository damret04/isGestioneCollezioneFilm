package is.progetto.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    /**
     * Creo un comando "Finto" (Mock) appositamente per il test.
     * Serve solo per verificare che il CommandManager chiami correttamente
     * i metodi esegui() e annulla() e gestisca lo storico.
     */
    class ComandoDiTest implements MediaCommand {
        boolean eseguito = false;

        @Override
        public boolean esegui() {
            eseguito = true; // Segna che l'azione è stata fatta
            return true;
        }

        @Override
        public boolean annulla() {
            eseguito = false; // Segna che l'azione è stata annullata
            return true;
        }

        @Override
        public String getDescrizione() {
            return "Comando di Test Fittizio";
        }
    }

    @Test
    void testGestioneCronologiaEUndo() {
        CommandManager manager = new CommandManager();
        ComandoDiTest comandoFinto = new ComandoDiTest();

        // 1. All'avvio del programma, non ci deve essere nulla da annullare
        assertFalse(manager.canUndo(), "Appena creato, lo storico deve essere vuoto");
        assertFalse(comandoFinto.eseguito, "Il comando non deve essere ancora eseguito");

        // 2. Eseguo il comando tramite il manager
        boolean esito = manager.eseguiCommand(comandoFinto);

        // Verifico che l'esecuzione sia andata a buon fine
        assertTrue(esito, "L'esecuzione deve restituire true");
        assertTrue(comandoFinto.eseguito, "Lo stato del comando deve essere 'eseguito'");
        assertTrue(manager.canUndo(), "Dopo l'esecuzione, lo storico deve avere un elemento da annullare");

        // 3. Faccio l'UNDO
        MediaCommand comandoAnnullato = manager.annulla();

        // Verifico che il manager abbia rimosso l'azione e ripristinato lo stato
        assertNotNull(comandoAnnullato, "Il manager deve restituire il comando annullato");
        assertFalse(comandoFinto.eseguito, "Lo stato del comando deve essere tornato a NON eseguito");
        assertFalse(manager.canUndo(), "Dopo aver annullato l'unica azione, lo storico deve tornare vuoto");
    }
}