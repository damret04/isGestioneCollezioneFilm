package is.progetto.controller;

import is.model.*;
import is.progetto.command.*;
import is.progetto.dao.*;

import java.util.List;

/*
 * Design Pattern: Facade
 * Agisce come punto di accesso per la View (Interfaccia Grafica).
 * Nasconde completamente la complessità del Database (DAO) e dello storico (Command).
 */

public class MediaController {

    private final MediaDao dao;
    private final CommandManager commandManager;

    public MediaController() {
        // Inizializzazione dei sottosistemi nascosti alla View
        this.dao = new MediaDaoImpl();
        this.commandManager = new CommandManager();
    }

    // METODI DI SCRITTURA (Passano per il Command Pattern per l'undo/redo)
    public boolean aggiungiNuovoMedia(ContenutiMultimediali media) {
        // 1. Cerca nel database se esiste già qualcosa con questo titolo
        List<ContenutiMultimediali> possibiliDuplicati = dao.filtraPerTitolo(media.getTitolo());

        // 2. Controlla se tra i risultati c'è un film con lo STESSO regista
        for (ContenutiMultimediali esistente : possibiliDuplicati) {
            if (esistente.getRegista().equalsIgnoreCase(media.getRegista())) {
                System.out.println("Salvataggio bloccato: Il film '" + media.getTitolo() + "' esiste già nel database!");
                return false; // Blocca immediatamente l'esecuzione e restituisce falso
            }
        }
        // Incapsula la richiesta in un comando e lo passa al manager
        MediaCommand comando = new AggiungiMediaCommand(dao, media);
        return commandManager.eseguiCommand(comando);
    }

    public boolean eliminaMedia(int id) {
        MediaCommand comando = new EliminaMediaCommand(dao, id);
        return commandManager.eseguiCommand(comando);
    }

    public boolean aggiornaMedia(ContenutiMultimediali mediaVecchio, ContenutiMultimediali mediaNuovo) {
        MediaCommand comando = new AggiornaMediaCommand(dao, mediaVecchio, mediaNuovo);
        return commandManager.eseguiCommand(comando);
    }

    public boolean annullaUltimaAzione() {
        MediaCommand comandoAnnullato = commandManager.annulla();
        // Restituisce true se l'annullamento è andato a buon fine
        return comandoAnnullato != null;
    }

    public boolean puoAnnullare() {
        return commandManager.canUndo();
    }

    // METODI DI LETTURA E FILTRAGGIO (Interrogano direttamente il DAO)
    public List<ContenutiMultimediali> getCollezioneCompleta() {
        return dao.getTutti();
    }

    public List<ContenutiMultimediali> cercaPerTitolo(String titolo) {
        return dao.filtraPerTitolo(titolo);
    }

    public List<ContenutiMultimediali> cercaPerRegista(String regista) {
        return dao.filtraPerRegista(regista);
    }

    public List<ContenutiMultimediali> filtraPerGenere(Genere genere) {
        return dao.filtraPerGenere(genere);
    }

    public List<ContenutiMultimediali> filtraPerStatoVisione(StatoVisione statoVisione) {
        return dao.filtraPerStatoVisione(statoVisione);
    }

    public List<ContenutiMultimediali> getOrdinatiPerTitolo() {
        return dao.getTuttiOrdinatiPerTitolo();
    }

    public List<ContenutiMultimediali> getOrdinatiPerAnno(boolean decrescente) {
        // Passo 'false' se l'utente li vuole dal più recente al più vecchio (decrescente)
        return dao.getTuttiOrdinatiPerAnno(!decrescente);
    }

    public List<ContenutiMultimediali> getOrdinatiPerValutazione(boolean decrescente) {
        return dao.getTuttiOrdinatiPerValutazione(!decrescente);
    }
}
