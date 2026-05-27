package is.progetto.persistence;

import java.sql.*;
public class InitDB {
    public static void init() {
        try(Connection conn = DBManager.getConnection();
            Statement stmt = conn.createStatement()){

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS film (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titolo VARCHAR(255) NOT NULL,
                    regista VARCHAR(255) NOT NULL,
                    anno_uscita INTEGER,
                    genere VARCHAR(100),
                    valutazione INTEGER CHECK(valutazione >= 1 AND valutazione <= 5),
                    stato_visione VARCHAR(50)
                );
            """
            );
            System.out.println("Tabella 'film' inizializzata con successo.");
        }catch(SQLException e){
            System.err.println("Errore durante l'inizializzazione del database:");
            e.printStackTrace();
        }
    }
}
