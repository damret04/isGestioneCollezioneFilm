package is.progetto.command;


/*
 * DESIGN PATTERN: COMMAND
 * Vado a definire il contratto per tutte le operazioni che vanno a modificare la collezione
 *
 */
public interface MediaCommand {
    // Esegue l'operazione
    boolean esegui();

    // Annulla l'operazione'
    boolean annulla();

    // Restituisce una descrizione dell'operazione
    String getDescrizione();

}