package is.progetto.view;

import is.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class MediaInputDialog extends Dialog<ContenutiMultimediali> {

    private TextField txtTitolo;
    private TextField txtRegista;
    // Uso Spinner per i numeri in modo che l'utente non inserisca testo dove non serve
    private Spinner<Integer> spinAnno;
    private ComboBox<Genere> comboGenere;
    private Spinner<Integer> spinValutazione;
    private ComboBox<StatoVisione> comboStato;
    private Spinner<Integer> spinDurata;

    public MediaInputDialog() {
        this.setTitle("Aggiungi Nuovo Film");
        this.setHeaderText("Inserisci i dettagli del nuovo film per la tua collezione.");

        // Imposta i bottoni del Dialog (Salva e Annulla)
        ButtonType btnSalvaType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnSalvaType, ButtonType.CANCEL);

        // Crea il layout a griglia (Form)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Inizializza i componenti di input
        txtTitolo = new TextField();
        txtTitolo.setPromptText("Es. Il Padrino");

        txtRegista = new TextField();
        txtRegista.setPromptText("Es. Francis Ford Coppola");

        // Spinner per i numeri (Valore min, Valore max, Valore di default)
        spinAnno = new Spinner<>(1888, 2026, 2024);
        spinAnno.setEditable(true);

        comboGenere = new ComboBox<>();
        comboGenere.getItems().setAll(Genere.values());
        comboGenere.setValue(Genere.ALTRO); // Default

        spinValutazione = new Spinner<>(1, 5, 3);

        comboStato = new ComboBox<>();
        comboStato.getItems().setAll(StatoVisione.values());
        comboStato.setValue(StatoVisione.DA_VEDERE); // Default

        spinDurata = new Spinner<>(1, 1000, 120);
        spinDurata.setEditable(true);

        // Aggiunge etichette e campi alla griglia (colonna, riga)
        grid.add(new Label("Titolo:"), 0, 0);
        grid.add(txtTitolo, 1, 0);
        grid.add(new Label("Regista:"), 0, 1);
        grid.add(txtRegista, 1, 1);
        grid.add(new Label("Anno Uscita:"), 0, 2);
        grid.add(spinAnno, 1, 2);
        grid.add(new Label("Genere:"), 0, 3);
        grid.add(comboGenere, 1, 3);
        grid.add(new Label("Valutazione (1-5):"), 0, 4);
        grid.add(spinValutazione, 1, 4);
        grid.add(new Label("Stato Visione:"), 0, 5);
        grid.add(comboStato, 1, 5);
        grid.add(new Label("Durata (min):"), 0, 6);
        grid.add(spinDurata, 1, 6);

        this.getDialogPane().setContent(grid);

        // Convertitore dei risultati: cosa succede quando l'utente clicca "Salva"?
        this.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvaType) {
                // Controllo base: Titolo e Regista non devono essere vuoti
                if (txtTitolo.getText().trim().isEmpty() || txtRegista.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Titolo e Regista sono obbligatori!");
                    alert.showAndWait();
                    return null; // Ferma il salvataggio se i campi sono vuoti
                }

                // Usa il Builder Pattern creato per istanziare il film
                return Film.builder()
                        .titolo(txtTitolo.getText().trim())
                        .regista(txtRegista.getText().trim())
                        .annoUscita(spinAnno.getValue())
                        .genere(comboGenere.getValue())
                        .valutazione(spinValutazione.getValue())
                        .statoVisione(comboStato.getValue())
                        .durata(spinDurata.getValue())
                        .build();
            }
            return null; // Se l'utente clicca "Annulla" o chiude la finestra
        });
    }
}