package is.progetto.persistence;

import java.sql.*;

public class InitDB {
    public static void init() {
        //pulisciDB();

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

            //popolaDatabase(conn);
        }
        catch(SQLException e){
            System.err.println("Errore durante l'inizializzazione del database:");
            e.printStackTrace();
        }
    }

    private static void popolaDatabase(Connection conn) {
        // Query di Multi-Insert: inserisce più righe contemporaneamente
        String sql = """
            INSERT INTO media (tipo_contenuto, titolo, regista, anno_uscita, genere, valutazione, stato_visione, durata)
            VALUES 
            ('Film', 'Inception', 'Christopher Nolan', 2010, 'FANTASCIENZA', 5, 'VISTO', 148),
            ('Film', 'Il Signore degli Anelli: La Compagnia dell\\'Anello', 'Peter Jackson', 2001, 'FANTASY', 5, 'VISTO', 178),
            ('Film', 'Matrix', 'Wachowski', 1999, 'FANTASCIENZA', 5, 'VISTO', 136),
            ('Film', 'Il Padrino', 'Francis Ford Coppola', 1972, 'AZIONE', 5, 'VISTO', 175),
            ('Film', 'Interstellar', 'Christopher Nolan', 2014, 'FANTASCIENZA', 4, 'VISTO', 169),
            ('Film', 'Pulp Fiction', 'Quentin Tarantino', 1994, 'AZIONE', 5, 'VISTO', 154),
            ('Film', 'Dune - Parte 1', 'Denis Villeneuve', 2021, 'FANTASCIENZA', 4, 'VISTO', 155),
            ('Film', 'Il Gladiatore', 'Ridley Scott', 2000, 'AZIONE', 4, 'VISTO', 155),
            ('Film', 'Spider-Man', 'Sam Raimi', 2002, 'AZIONE', 3, 'VISTO', 121),
            ('Film', 'Oppenheimer', 'Christopher Nolan', 2023, 'DRAMMA', 4, 'DA_VEDERE', 180)
        """;

        try (Statement stmt = conn.createStatement()) {
            int righeInserite = stmt.executeUpdate(sql);
            System.out.println("Database popolato con successo con " + righeInserite + " capolavori cinematografici!");
        } catch (SQLException e) {
            System.err.println("Errore durante il popolamento del database: " + e.getMessage());
        }
    }

    /**
     * Metodo statico richiamabile da qualsiasi classe per
     * svuotare la tabella e resettare il contatore degli ID.
     */
    public static void pulisciDB() {
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Svuota la tabella
            stmt.execute("DELETE FROM media");

            // Resetta l'AUTO_INCREMENT a 1
            // Così i test avranno sempre ID puliti e prevedibili
            stmt.execute("ALTER TABLE media AUTO_INCREMENT = 1");

        } catch (Exception e) {
            System.err.println("Impossibile pulire il database di test: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        init();
    }
}