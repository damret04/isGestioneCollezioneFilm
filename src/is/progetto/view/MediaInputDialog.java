package is.progetto.view;

import is.model.ContenutiMultimediali;
import is.model.Film;
import is.model.Genere;
import is.model.StatoVisione;
import is.progetto.factory.FilmFactory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class MediaInputDialog extends Dialog<ContenutiMultimediali> {

    // Selettore del tipo di contenuto (fulcro dell'estensibilità)
    private ComboBox<String> comboTipo;

    // Campi comuni a tutti i contenuti multimediali
    private TextField txtTitolo;
    private TextField txtRegista;
    private Spinner<Integer> spinAnno;
    private ComboBox<Genere> comboGenere;
    private Spinner<Integer> spinValutazione;
    private ComboBox<StatoVisione> comboStato;

    // Campi specifici (es. per il Film)
    private Label lblDurata;
    private Spinner<Integer> spinDurata;

    public MediaInputDialog(ContenutiMultimediali mediaDaModificare) {
        boolean isModifica = (mediaDaModificare != null);

        // Titoli generici (non più "Film")
        this.setTitle(isModifica ? "Modifica Contenuto" : "Aggiungi Nuovo Contenuto");
        this.setHeaderText(isModifica ? "Modifica i dettagli di: " + mediaDaModificare.getTitolo() : "Inserisci i dettagli del nuovo contenuto.");

        ButtonType btnSalvaType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnSalvaType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 1. CONFIGURAZIONE DEL TIPO CONTENUTO
        comboTipo = new ComboBox<>();
        // ESTENSIBILITA': Quando si vorrà creare ad esempio SerieTv, basterà aggiungere
        // SerieTv.TIPO_CONTENUTO a questa lista
        comboTipo.getItems().addAll(Film.TIPO_CONTENUTO);

        if (isModifica) {
            comboTipo.setValue(mediaDaModificare.getTipoContenuto());
            comboTipo.setDisable(true); // Non ha senso trasformare un Film in una SerieTV durante la modifica
        } else {
            comboTipo.setValue(Film.TIPO_CONTENUTO); // Selezione di default
        }

        // 2. PRECOMPILAZIONE CAMPI COMUNI
        txtTitolo = new TextField(isModifica ? mediaDaModificare.getTitolo() : "");
        txtRegista = new TextField(isModifica ? mediaDaModificare.getRegista() : "");
        spinAnno = new Spinner<>(1888, 2026, isModifica ? mediaDaModificare.getAnnoUscita() : 2024);
        spinAnno.setEditable(true);
        comboGenere = new ComboBox<>();
        comboGenere.getItems().setAll(Genere.values());
        comboGenere.setValue(isModifica ? mediaDaModificare.getGenere() : Genere.ALTRO);
        spinValutazione = new Spinner<>(1, 5, isModifica ? mediaDaModificare.getValutazione() : 3);
        comboStato = new ComboBox<>();
        comboStato.getItems().setAll(StatoVisione.values());
        comboStato.setValue(isModifica ? mediaDaModificare.getStatoVisione() : StatoVisione.DA_VEDERE);

        // 3. CAMPI SPECIFICI (es. Film)
        lblDurata = new Label("Durata (min):");
        int durataPrecedente = 120;
        if (isModifica && mediaDaModificare instanceof Film f) {
            durataPrecedente = f.getDurata();
        }
        spinDurata = new Spinner<>(1, 1000, durataPrecedente);
        spinDurata.setEditable(true);

        // 4. GESTIONE DINAMICA DELL'INTERFACCIA
        // Se l'utente cambia tipo nella tendina, vengono mostrati o nascosti i campi specifici
        comboTipo.valueProperty().addListener((obs, oldVal, newVal) -> aggiornaVisibilitaCampi(newVal));

        // 5. AGGIUNTA ALLA GRIGLIA
        int row = 0;
        grid.add(new Label("Tipo:"), 0, row);
        grid.add(comboTipo, 1, row++);
        grid.add(new Label("Titolo:"), 0, row);
        grid.add(txtTitolo, 1, row++);
        grid.add(new Label("Regista:"), 0, row);
        grid.add(txtRegista, 1, row++);
        grid.add(new Label("Anno Uscita:"), 0, row);
        grid.add(spinAnno, 1, row++);
        grid.add(new Label("Genere:"), 0, row);
        grid.add(comboGenere, 1, row++);
        grid.add(new Label("Valutazione:"), 0, row);
        grid.add(spinValutazione, 1, row++);
        grid.add(new Label("Stato:"), 0, row);
        grid.add(comboStato, 1, row++);

        // Campi specifici
        grid.add(lblDurata, 0, row);
        grid.add(spinDurata, 1, row++);

        // Forza l'aggiornamento visivo iniziale
        aggiornaVisibilitaCampi(comboTipo.getValue());

        this.getDialogPane().setContent(grid);

        // 6. LOGICA DI CREAZIONE (ABSTRACT FACTORY)
        this.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvaType) {
                if (txtTitolo.getText().trim().isEmpty() || txtRegista.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Titolo e Regista obbligatori!");
                    alert.showAndWait();
                    return null;
                }

                String tipoScelto = comboTipo.getValue();
                int id = isModifica ? mediaDaModificare.getId() : 0;

                // Delegare la creazione alla Factory corretta
                if (Film.TIPO_CONTENUTO.equals(tipoScelto)) {

                    // 1. Uso la Factory Singleton per creare l'oggetto base
                    ContenutiMultimediali nuovoMedia = FilmFactory.getInstance().crea(
                            id,
                            txtTitolo.getText().trim(),
                            txtRegista.getText().trim(),
                            spinAnno.getValue(),
                            spinValutazione.getValue()
                    );

                    // 2. Imposta le proprietà definite nell'interfaccia
                    nuovoMedia.setGenere(comboGenere.getValue());
                    nuovoMedia.setStatoVisione(comboStato.getValue());

                    // 3. Imposta le proprietà specifiche del Film
                    if (nuovoMedia instanceof Film film) {
                        film.setDurata(spinDurata.getValue());
                    }

                    return nuovoMedia;
                }

                // ESTENSIBILITÀ FUTURA:
                // else if (SerieTv.TIPO_CONTENUTO.equals(tipoScelto)) {
                //      ContenutiMultimediali nuovaSerie = SerieTvFactory.getInstance().crea(...);
                //      ...
                //      return nuovaSerie;
                // }
            }
            return null;
        });
    }

    /**
     * Metodo helper per nascondere i campi che non appartengono al tipo selezionato.
     * Sfrutta setManaged(false) per fare in modo che i campi nascosti non occupino spazio vuoto.
     */
    private void aggiornaVisibilitaCampi(String tipoSelezionato) {
        boolean isFilm = Film.TIPO_CONTENUTO.equals(tipoSelezionato);

        lblDurata.setVisible(isFilm);
        lblDurata.setManaged(isFilm);
        spinDurata.setVisible(isFilm);
        spinDurata.setManaged(isFilm);

        // ESTENSIBILITA':
        // boolean isSerie = SerieTv.TIPO_CONTENUTO.equals(tipoSelezionato);
        // lblStagioni.setVisible(isSerie);
        // ...
    }
}