package is.progetto;

import is.progetto.view.MediaView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Istanzia la vista principale
        MediaView root = new MediaView();

        // Crea la scena (800x600 pixel)
        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Gestione Collezione Film - MVC");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Necessario per far partire l'inizializzazione del Database prima della grafica
        is.progetto.persistence.InitDB.init();
        launch(args);
    }
}