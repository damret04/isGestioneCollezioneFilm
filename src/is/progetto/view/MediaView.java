package is.progetto.view;

import is.model.*;
import is.progetto.controller.MediaController;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert.AlertType;

public class MediaView extends BorderPane {

    // Facade: l'unico punto di contatto con la logica di business
    private final MediaController facade;

    // Componenti Visivi
    private TableView<ContenutiMultimediali> table;
    private ObservableList<ContenutiMultimediali> datiOsservabili;

    // Pulsanti
    private Button btnAggiungi;
    private Button btnElimina;
    private Button btnModifica;
    private Button btnAnnulla;

    public MediaView() {
        this.facade = new MediaController();
        this.datiOsservabili = FXCollections.observableArrayList();

        inizializzaInterfaccia();
        aggiornaDatiTabella();
    }

    private void inizializzaInterfaccia() {
        this.setPadding(new Insets(10));

        // 1. TOP: Barra di ricerca e filtri
        HBox topBar = creaBarraSuperiore();
        this.setTop(topBar);

        // 2. CENTER: La Tabella
        table = creaTabella();
        this.setCenter(table);

        // 3. BOTTOM: Barra dei bottoni (Aggiungi, Elimina, Undo)
        HBox bottomBar = creaBarraInferiore();
        this.setBottom(bottomBar);
    }

    private HBox creaBarraSuperiore() {
        HBox hbox = new HBox(15); // Spaziatura tra gli elementi
        hbox.setPadding(new Insets(0, 0, 15, 0));

        // 1. CAMPO DI RICERCA TESTUALE (Titolo)
        TextField txtCerca = new TextField();
        txtCerca.setPromptText("Cerca per titolo...");

        Button btnCerca = new Button("Cerca");
        btnCerca.setOnAction(e -> {
            String ricerca = txtCerca.getText();
            if (ricerca != null && !ricerca.isEmpty()) {
                datiOsservabili.setAll(facade.cercaPerTitolo(ricerca));
            } else {
                aggiornaDatiTabella();
            }
        });

        // 2. FILTRO PER GENERE (ComboBox)
        ComboBox<Genere> comboFiltroGenere = new ComboBox<>();
        comboFiltroGenere.setPromptText("Filtra per Genere");
        comboFiltroGenere.getItems().setAll(Genere.values());

        // Evento: quando l'utente sceglie un genere, viene filtrata la tabella
        comboFiltroGenere.setOnAction(e -> {
            Genere selezionato = comboFiltroGenere.getValue();
            if (selezionato != null) {
                datiOsservabili.setAll(facade.filtraPerGenere(selezionato));
            }
        });

        // 3. ORDINAMENTO (ComboBox)
        ComboBox<String> comboOrdinamento = new ComboBox<>();
        comboOrdinamento.setPromptText("Ordina per...");
        comboOrdinamento.getItems().addAll("Titolo (A-Z)", "Anno (Più recenti)", "Valutazione (Migliori)");

        // Evento: quando l'utente sceglie un ordinamento, vengono chiesti alla Facade i dati ordinati
        comboOrdinamento.setOnAction(e -> {
            String scelta = comboOrdinamento.getValue();
            if (scelta == null) return;

            switch (scelta) {
                case "Titolo (A-Z)" -> datiOsservabili.setAll(facade.getOrdinatiPerTitolo());
                case "Anno (Più recenti)" -> datiOsservabili.setAll(facade.getOrdinatiPerAnno(true));
                case "Valutazione (Migliori)" -> datiOsservabili.setAll(facade.getOrdinatiPerValutazione(true));
            }
        });

        // 4. TASTO RESET (Azzera tutti i filtri)
        Button btnReset = new Button("Mostra Tutti");
        btnReset.setOnAction(e -> {
            txtCerca.clear();
            comboFiltroGenere.setValue(null);
            comboOrdinamento.setValue(null);
            aggiornaDatiTabella(); // Ricarica la lista base
        });

        // Tutti i controlli sulla barra orizzontale
        hbox.getChildren().addAll(
                new Label("Ricerca:"), txtCerca, btnCerca,
                new Label("Genere:"), comboFiltroGenere,
                new Label("Ordina:"), comboOrdinamento,
                btnReset
        );

        // Allineamento verticale al centro per i componenti della barra
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        return hbox;
    }

    private TableView<ContenutiMultimediali> creaTabella() {
        TableView<ContenutiMultimediali> t = new TableView<>();

        // Creazione delle Colonne
        TableColumn<ContenutiMultimediali, String> colTitolo = new TableColumn<>("Titolo");
        colTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));

        TableColumn<ContenutiMultimediali, String> colRegista = new TableColumn<>("Regista");
        colRegista.setCellValueFactory(new PropertyValueFactory<>("regista"));

        TableColumn<ContenutiMultimediali, Integer> colAnno = new TableColumn<>("Anno");
        colAnno.setCellValueFactory(new PropertyValueFactory<>("annoUscita"));

        TableColumn<ContenutiMultimediali, String> colGenere = new TableColumn<>("Genere");
        colGenere.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGenere().getNome()));

        TableColumn<ContenutiMultimediali, Integer> colValutazione = new TableColumn<>("Voto");
        colValutazione.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getValutazione()));

        TableColumn<ContenutiMultimediali, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatoVisione().getDescrizione()));

        // Aggiunge le colonne alla tabella
        t.getColumns().addAll(colTitolo, colRegista, colAnno, colGenere, colValutazione, colStato);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Adatta le colonne alla larghezza

        // Collegha l'Observer alla Tabella
        t.setItems(datiOsservabili);

        return t;
    }

    private HBox creaBarraInferiore() {
        HBox hbox = new HBox(15);
        hbox.setPadding(new Insets(10, 0, 0, 0));

        btnAggiungi = new Button("Aggiungi");
        btnModifica = new Button("Modifica");
        btnElimina = new Button("Elimina");
        btnAnnulla = new Button("Annulla Azione");
        btnAnnulla.setDisable(true);

        // GESTIONE EVENTI
        // 1. Azione AGGIUNGI
        btnAggiungi.setOnAction(e -> {
            MediaInputDialog dialog = new MediaInputDialog(null);
            dialog.showAndWait().ifPresent(nuovoMedia -> {

                // Richiama il controller. Ora restituisce TRUE se salva, FALSE se è un clone
                boolean salvataggioRiuscito = facade.aggiungiNuovoMedia(nuovoMedia);

                if (salvataggioRiuscito) {
                    aggiornaDatiTabella();
                } else {
                    // Crea l'Alert grafico di Errore
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Errore di Salvataggio");
                    alert.setHeaderText("Contenuto Duplicato!");
                    alert.setContentText("Il film '" + nuovoMedia.getTitolo() + "' di '" + nuovoMedia.getRegista() + "' è già presente nel database.");
                    alert.showAndWait();
                }
            });
        });

        // 2. Azione MODIFICA
        btnModifica.setOnAction(e -> {
            ContenutiMultimediali selezionato = table.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                MediaInputDialog dialog = new MediaInputDialog(selezionato); // <-- Nuovo nome classe
                dialog.showAndWait().ifPresent(mediaModificato -> {
                    if(facade.aggiornaMedia(selezionato, mediaModificato)) {
                        aggiornaDatiTabella();
                    }
                });
            } else {
                mostraAllerta("Attenzione", "Seleziona un elemento dalla tabella.");
            }
        });

        // 3. Azione ELIMINA
        btnElimina.setOnAction(e -> {
            ContenutiMultimediali selezionato = table.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                if(facade.eliminaMedia(selezionato.getId())) {
                    aggiornaDatiTabella();
                }
            } else {
                mostraAllerta("Attenzione", "Seleziona un film dalla tabella per eliminarlo.");
            }
        });

        // 4. Azione ANNULLA (UNDO)
        btnAnnulla.setOnAction(e -> {
            if(facade.annullaUltimaAzione()) {
                aggiornaDatiTabella();
            }
        });

        // Aggiunge tutti e 4 i bottoni all'interfaccia
        hbox.getChildren().addAll(btnAggiungi, btnModifica, btnElimina, btnAnnulla);
        return hbox;
    }

    // Metodo helper per ricaricare la tabella e aggiornare lo stato del bottone Undo
    private void aggiornaDatiTabella() {
        datiOsservabili.setAll(facade.getCollezioneCompleta());
        btnAnnulla.setDisable(!facade.puoAnnullare()); // Abilita l'Undo solo se ci sono comandi nella cronologia
    }

    private void mostraAllerta(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}