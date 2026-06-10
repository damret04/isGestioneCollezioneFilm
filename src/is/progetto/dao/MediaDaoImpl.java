package is.progetto.dao;

import is.progetto.persistence.DBManager;

import is.model.*;
import is.progetto.persistence.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaDaoImpl implements MediaDao {

    public final DBManager dbManager = DBManager.getInstance();

    public MediaDaoImpl() {
    }

    @Override
    public void salva(ContenutiMultimediali media) {
        // L'id è AUTO_INCREMENT, non va inserito
        String sql = """
                INSERT INTO film (
                    titolo, regista, anno_uscita, genere, valutazione, stato_visione, durata
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, media.getTitolo());
            pstmt.setString(2, media.getRegista());
            pstmt.setInt(3, media.getAnnoUscita());
            pstmt.setString(4, media.getGenere() != null ? media.getGenere().name() : Genere.ALTRO.name());
            pstmt.setInt(5, media.getValutazione());
            pstmt.setString(6, media.getStatoVisione() != null ? media.getStatoVisione().name() : StatoVisione.DA_VEDERE.name());

            // Gestione dei campi specifici (es. durata) se è un Film
            if (media instanceof Film film) {
                pstmt.setInt(7, film.getDurata());
            } else {
                pstmt.setObject(7, null); // Se in futuro aggiungi SerieTv che non ha durata
            }

            pstmt.executeUpdate();
            System.out.println("Media salvato con successo: " + media.getTitolo());

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del media: " + media.getTitolo(), e);
        }
    }

    @Override
    public List<ContenutiMultimediali> getTutti() {
        List<ContenutiMultimediali> lista = new ArrayList<>();
        String sql = "SELECT * FROM film"; // Aggiungi ORDER BY se preferisci

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ContenutiMultimediali media = estraiMediaDaResultSet(rs);
                if (media != null) {
                    lista.add(media);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero di tutti i media", e);
        }
        return lista;
    }

    // N.B: Ho cambiato l'ID in int come avevamo detto per il nuovo modello.
    // Se nella tua interfaccia MediaDao restituisci una List, cambia in List<ContenutiMultimediali>.
    // L'ideale sarebbe restituire un singolo oggetto.
    @Override
    public Optional<ContenutiMultimediali> trovaPerId(int id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(estraiMediaDaResultSet(rs));
                }
            }
            return null; // O Optional.empty() se aggiorno l'interfaccia
        } catch (SQLException e) {
            throw new RuntimeException("Errore ricerca per ID: " + id, e);
        }
    }

    @Override
    public void aggiorna(ContenutiMultimediali media) {
        String sql = """
                UPDATE film SET 
                titolo=?, regista=?, anno_uscita=?, genere=?, valutazione=?, stato_visione=?, durata=?
                WHERE id = ?
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, media.getTitolo());
            pstmt.setString(2, media.getRegista());
            pstmt.setInt(3, media.getAnnoUscita());
            pstmt.setString(4, media.getGenere() != null ? media.getGenere().name() : Genere.ALTRO.name());
            pstmt.setInt(5, media.getValutazione());
            pstmt.setString(6, media.getStatoVisione() != null ? media.getStatoVisione().name() : StatoVisione.DA_VEDERE.name());

            if (media instanceof Film film) {
                pstmt.setInt(7, film.getDurata());
            } else {
                pstmt.setObject(7, null);
            }

            pstmt.setInt(8, media.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiornamento media: " + media.getTitolo(), e);
        }
    }

    @Override
    public void elimina(int id) {
        String sql = "DELETE FROM film WHERE id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore eliminazione media con ID: " + id, e);
        }
    }

    // METODI DI FILTRAGGIO

    @Override
    public List<ContenutiMultimediali> filtraPerTitolo(String titolo) {
        return eseguiRicercaFiltro("SELECT * FROM film WHERE LOWER(titolo) LIKE ?", "%" + titolo.toLowerCase() + "%");
    }

    @Override
    public List<ContenutiMultimediali> filtraPerRegista(String regista) {
        return eseguiRicercaFiltro("SELECT * FROM film WHERE LOWER(regista) LIKE ?", "%" + regista.toLowerCase() + "%");
    }

    @Override
    public List<ContenutiMultimediali> filtraPerGenere(Genere genere) {
        return eseguiRicercaFiltro("SELECT * FROM film WHERE genere = ?", genere.name());
    }

    @Override
    public List<ContenutiMultimediali> filtraPerStatoVisione(StatoVisione statoVisione) {
        return eseguiRicercaFiltro("SELECT * FROM film WHERE stato_visione = ?", statoVisione.name());
    }

    @Override
    public List<ContenutiMultimediali> getTuttiOrdinatiPerTitolo() {
        return eseguiRicercaOrdinata("SELECT * FROM film ORDER BY titolo ASC");
    }

    @Override
    public List<ContenutiMultimediali> getTuttiOrdinatiPerAnno(boolean crescente) {
        String ordine = crescente ? "ASC" : "DESC";
        return eseguiRicercaOrdinata("SELECT * FROM film ORDER BY anno_uscita " + ordine);
    }

    @Override
    public List<ContenutiMultimediali> getTuttiOrdinatiPerValutazione(boolean crescente) {
        String ordine = crescente ? "ASC" : "DESC";
        return eseguiRicercaOrdinata("SELECT * FROM film ORDER BY valutazione " + ordine);
    }

    // METODI DI SUPPORTO

    private List<ContenutiMultimediali> eseguiRicercaFiltro(String sql, String parametro) {
        List<ContenutiMultimediali> risultati = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parametro);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ContenutiMultimediali media = estraiMediaDaResultSet(rs);
                    if (media != null) risultati.add(media);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca/filtro", e);
        }
        return risultati;
    }

    private List<ContenutiMultimediali> eseguiRicercaOrdinata(String sql) {
        List<ContenutiMultimediali> lista = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ContenutiMultimediali media = estraiMediaDaResultSet(rs);
                if (media != null) {
                    lista.add(media);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei dati ordinati", e);
        }
        return lista;
    }

    private ContenutiMultimediali estraiMediaDaResultSet(ResultSet rs) throws SQLException {
        // Estraiamo i dati dalla riga del Database
        int id = rs.getInt("id");
        String titolo = rs.getString("titolo");
        String regista = rs.getString("regista");
        int annoUscita = rs.getInt("anno_uscita");

        // Uso valueOf per convertire la stringa del DB nell'Enum corrispondente
        Genere genere = Genere.valueOf(rs.getString("genere"));
        int valutazione = rs.getInt("valutazione");
        StatoVisione statoVisione = StatoVisione.valueOf(rs.getString("stato_visione"));
        int durata = rs.getInt("durata"); // Assumendo che ci sia la colonna durata

        // Uso il Pattern Builder per ricostruire l'oggetto
        // Se in futuro dovessi aggiungere altri contenuti, potrò inserire qui un if/switch che usa il Builder corretto
        // in base al campo "tipo_contenuto" se dovessi decidere di implementarlo.
        return Film.builder()
                .id(id)
                .titolo(titolo)
                .regista(regista)
                .annoUscita(annoUscita)
                .genere(genere)
                .valutazione(valutazione)
                .statoVisione(statoVisione)
                .durata(durata)
                .build();
    }
}
