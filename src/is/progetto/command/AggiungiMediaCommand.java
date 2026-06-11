package is.progetto.command;

import is.model.ContenutiMultimediali;
import is.progetto.dao.MediaDao;

public class AggiungiMediaCommand implements MediaCommand {
    private final MediaDao dao;
    private final ContenutiMultimediali contenuto;

    private int idGenerato = -1; // Salva qui l'ID appena il database lo crea
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
                this.idGenerato = dao.getUltimoIdInserito(); // Ora il comando sa quale ID eliminare se fai Undo
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
                dao.elimina(this.idGenerato);
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
