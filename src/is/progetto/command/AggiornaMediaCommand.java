package is.progetto.command;

import is.model.ContenutiMultimediali;
import is.progetto.dao.MediaDao;

public class AggiornaMediaCommand implements MediaCommand {
    private final MediaDao dao;
    private final ContenutiMultimediali contenuto;
    private ContenutiMultimediali contenutoVecchio; // Backup per l'Undo

    private boolean successo = false;

    public AggiornaMediaCommand(MediaDao dao, ContenutiMultimediali contenutoVecchio, ContenutiMultimediali contenuto) {
        this.dao = dao;
        this.contenutoVecchio = contenutoVecchio;
        this.contenuto = contenuto;
    }

    @Override
    public boolean esegui() {
        if (!successo) {
            try {
                // Per aggiornare sul database, va controllato che il nuovo oggetto abbia lo stesso ID del vecchio
                dao.aggiorna(contenuto);
                successo = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean annulla() {
        if (successo) {
            try {
                // Ripristino i vecchi dati sul database
                dao.aggiorna(contenutoVecchio);
                successo = false;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public String getDescrizione() {
        return "Modificato film: " + contenutoVecchio.getTitolo();
    }
}