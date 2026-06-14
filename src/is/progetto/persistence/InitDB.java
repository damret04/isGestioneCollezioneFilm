package is.progetto.persistence;

import java.sql.*;

public class InitDB {
    public static void init() {
        try(Connection conn = DBManager.getConnection();
            Statement stmt = conn.createStatement()){

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS media (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    tipo_contenuto VARCHAR(50) NOT NULL,
                    titolo VARCHAR(255) NOT NULL,
                    regista VARCHAR(255) NOT NULL,
                    anno_uscita INT,
                    genere VARCHAR(100),
                    valutazione INT CHECK(valutazione >= 1 AND valutazione <= 5),
                    stato_visione VARCHAR(50),
                    durata INT
                );
            """);
            System.out.println("Tabella 'media' inizializzata con successo.");
        }
        catch(SQLException e){
            System.err.println("Errore durante l'inizializzazione del database:");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        init();
    }
}