package is.progetto.command;

import is.model.ContenutiMultimediali;
import is.progetto.dao.MediaDao;

public class AggiungiMediaCommand implements MediaCommand {
    private final MediaDao dao;
    private final ContenutiMultimediali contenuto;

    private boolean successo = false;

    public AggiungiMediaCommand(MediaDao dao, ContenutiMultimediali contenuto) {
        this.dao = dao;
        this.contenuto = contenuto;
    }

    @Override
    public boolean esegui(){
        if(!successo) {
            try {
                dao.salva(contenuto);
                successo = true;
                return true;
            }catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean annulla() {
        if (!successo) {
            try {
                dao.elimina(contenuto.getId());
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
        return "Aggiunta: " + contenuto.getTitolo();
    }
}
