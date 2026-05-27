package is.progetto.persistence;

import java.sql.*;

/*
* Database Manager che gestiche la connessione al database H2
*
* Design Pattern: Singleton
* - Unica istanza per gestire le connessioni
*
 */

public class DBManager {

    private static DBManager instance;

    private static final String url = "jdbc:h2:./db/films";
    private static final String user = "sa";
    private static final String password = "";

    private static Connection conn;

    private DBManager() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        try{
            if(conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
            }
        }catch(SQLException e){
            conn = DriverManager.getConnection(url, user, password);
        }
        return conn;
    }

    public static DBManager getInstance() {
        if(instance == null) {
            synchronized (DBManager.class) {
                if(instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }


    public String getDataBasePath() {
        return url;
    }
}
