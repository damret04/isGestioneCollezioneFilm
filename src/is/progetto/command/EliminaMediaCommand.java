package is.progetto.command;

import is.model.ContenutiMultimediali;
import is.progetto.dao.MediaDao;

public class EliminaMediaCommand implements MediaCommand {
    private final MediaDao dao;
    private final int idDaEliminare;

    // Mantiene lo stato dell'oggetto prima che venisse distrutto per poterlo ripristinare
    private ContenutiMultimediali backupContenuto;
    private boolean successo = false;

    public EliminaMediaCommand(MediaDao dao, int idDaEliminare) {
        this.dao = dao;
        this.idDaEliminare = idDaEliminare;
    }

    @Override
    public boolean esegui() {
        if (!successo) {
            try {
                // 1. Prima di eliminare, recupera l'oggetto dal DB e ne faccio il backup
                this.backupContenuto = dao.trovaPerId(idDaEliminare).orElse(null);

                if (this.backupContenuto != null) {
                    // 2. Procede con l'eliminazione
                    dao.elimina(idDaEliminare);
                    successo = true;
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean annulla() {
        // Se viene eliminato con successo, ripristina l'oggetto dal backup
        if (successo && backupContenuto != null) {
            try {
                dao.salva(backupContenuto);
                successo = false;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getDescrizione() {
        if (backupContenuto != null) {
            return "Eliminato: " + backupContenuto.getTitolo();
        }
        return "Eliminazione ID: " + idDaEliminare;
    }
}
