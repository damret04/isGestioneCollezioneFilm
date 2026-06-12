package is.progetto.view;

import is.model.ContenutiMultimediali;
import is.model.Film;
import is.model.Genere;
import is.model.StatoVisione;
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
import javafx.scene.layout.VBox;

public class MediaView extends BorderPane {

    // Facade: l'unico punto di contatto con la logica di business
    private final MediaController facade;

    // Componenti Visivi
    private TableView<ContenutiMultimediali> table;
    private ObservableList<ContenutiMultimediali> datiOsservabili;

    // Pulsanti
    private Button btnAggiungi;
    private Button btnElimina;
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
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(0, 0, 10, 0));

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

        Button btnReset = new Button("Mostra Tutti");
        btnReset.setOnAction(e -> {
            txtCerca.clear();
            aggiornaDatiTabella();
        });

        hbox.getChildren().addAll(new Label("Ricerca:"), txtCerca, btnCerca, btnReset);
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

        btnAggiungi = new Button("Aggiungi Nuovo Film");
        btnElimina = new Button("Elimina Selezionato");
        btnAnnulla = new Button("Annulla Ultima Azione (Undo)");
        btnAnnulla.setDisable(true); // Disabilitato all'avvio perché non c'è nulla da annullare

        // GESTIONE EVENTI (Il cuore del Controller visivo)

        btnAggiungi.setOnAction(e -> {
            MediaInputDialog dialog = new MediaInputDialog();

            // showAndWait() blocca l'esecuzione finché l'utente non chiude il popup
            dialog.showAndWait().ifPresent(nuovoFilm -> {

                // Se il dialog restituisce un film (utente ha cliccato "Salva"), lo passa alla Facade
                if(facade.aggiungiNuovoMedia(nuovoFilm)) {
                    aggiornaDatiTabella(); // Il film compare in tabella
                } else {
                    mostraAllerta("Errore", "Si è verificato un problema durante il salvataggio nel Database.");
                }
            });
        });

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

        btnAnnulla.setOnAction(e -> {
            if(facade.annullaUltimaAzione()) {
                aggiornaDatiTabella();
            }
        });

        hbox.getChildren().addAll(btnAggiungi, btnElimina, btnAnnulla);
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