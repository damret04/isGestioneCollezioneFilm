package is.progetto.dao;

import is.progetto.persistence.DBManager;
import is.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaDaoImpl implements MediaDao {

    public final DBManager dbManager = DBManager.getInstance();

    public MediaDaoImpl() {}

    @Override
    public void salva(ContenutiMultimediali media) {

        String sql = """
                INSERT INTO media (
                    tipo_contenuto, titolo, regista, anno_uscita, genere, valutazione, stato_visione, durata
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, media.getTipoContenuto());
            pstmt.setString(2, media.getTitolo());
            pstmt.setString(3, media.getRegista());
            pstmt.setInt(4, media.getAnnoUscita());
            pstmt.setString(5, media.getGenere() != null ? media.getGenere().name() : Genere.ALTRO.name());
            pstmt.setInt(6, media.getValutazione());
            pstmt.setString(7, media.getStatoVisione() != null ? media.getStatoVisione().name() : StatoVisione.DA_VEDERE.name());

            if (media instanceof Film film) {
                pstmt.setInt(8, film.getDurata());
            } else {
                pstmt.setObject(8, null);
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
        String sql = "SELECT * FROM media";

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ContenutiMultimediali media = estraiMediaDaResultSet(rs);
                if (media != null) lista.add(media);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero di tutti i media", e);
        }
        return lista;
    }

    @Override
    public Optional<ContenutiMultimediali> trovaPerId(int id) {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(estraiMediaDaResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Errore ricerca per ID: " + id, e);
        }
    }

    @Override
    public void aggiorna(ContenutiMultimediali media) {
        String sql = """
                UPDATE media SET 
                tipo_contenuto=?, titolo=?, regista=?, anno_uscita=?, genere=?, valutazione=?, stato_visione=?, durata=?
                WHERE id = ?
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, media.getTipoContenuto());
            pstmt.setString(2, media.getTitolo());
            pstmt.setString(3, media.getRegista());
            pstmt.setInt(4, media.getAnnoUscita());
            pstmt.setString(5, media.getGenere() != null ? media.getGenere().name() : Genere.ALTRO.name());
            pstmt.setInt(6, media.getValutazione());
            pstmt.setString(7, media.getStatoVisione() != null ? media.getStatoVisione().name() : StatoVisione.DA_VEDERE.name());

            if (media instanceof Film film) {
                pstmt.setInt(8, film.getDurata());
            } else {
                pstmt.setObject(8, null);
            }

            pstmt.setInt(9, media.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiornamento media: " + media.getTitolo(), e);
        }
    }

    @Override
    public void elimina(int id) {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore eliminazione media con ID: " + id, e);
        }
    }

    //  METODI DI RICERCA E ORDINAMENTO

    @Override
    public List<ContenutiMultimediali> filtraPerTitolo(String titolo) {
        return eseguiRicercaFiltro("SELECT * FROM media WHERE LOWER(titolo) LIKE ?", "%" + titolo.toLowerCase() + "%");
    }

    @Override
    public List<ContenutiMultimediali> filtraPerRegista(String regista) {
        return eseguiRicercaFiltro("SELECT * FROM media WHERE LOWER(regista) LIKE ?", "%" + regista.toLowerCase() + "%");
    }

    @Override
    public List<ContenutiMultimediali> filtraPerGenere(Genere genere) {
        return eseguiRicercaFiltro("SELECT * FROM media WHERE genere = ?", genere.name());
    }

    @Override
    public List<ContenutiMultimediali> filtraPerStatoVisione(StatoVisione statoVisione) {
        return eseguiRicercaFiltro("SELECT * FROM media WHERE stato_visione = ?", statoVisione.name());
    }

    @Override
    public List<ContenutiMultimediali> getTuttiOrdinatiPerTitolo() {
        return eseguiRicercaOrdinata("SELECT * FROM media ORDER BY titolo ASC");
    }

    @Override
    public List<ContenutiMultimediali> getTuttiOrdinatiPerAnno(boolean crescente) {
        String ordine = crescente ? "ASC" : "DESC";
        return eseguiRicercaOrdinata("SELECT * FROM media ORDER BY anno_uscita " + ordine);
    }

    @Override
    public List<ContenutiMultimediali> getTuttiOrdinatiPerValutazione(boolean crescente) {
        String ordine = crescente ? "ASC" : "DESC";
        return eseguiRicercaOrdinata("SELECT * FROM media ORDER BY valutazione " + ordine);
    }

    @Override
    public int getUltimoIdInserito() {
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM media")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //  METODI DI SUPPORTO PRIVATI

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
                if (media != null) lista.add(media);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei dati ordinati", e);
        }
        return lista;
    }

    private ContenutiMultimediali estraiMediaDaResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipoContenuto = rs.getString("tipo_contenuto");
        String titolo = rs.getString("titolo");
        String regista = rs.getString("regista");
        int annoUscita = rs.getInt("anno_uscita");
        Genere genere = Genere.valueOf(rs.getString("genere"));
        int valutazione = rs.getInt("valutazione");
        StatoVisione statoVisione = StatoVisione.valueOf(rs.getString("stato_visione"));

        // ESTENSIBILITÀ: Instanzia l'oggetto corretto in base alla colonna "tipo_contenuto"
        if (tipoContenuto != null && tipoContenuto.equalsIgnoreCase("Film")) {
            int durata = rs.getInt("durata");
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

        // In futuro: else if (SerieTv.TIPO_CONTENUTO.equals(tipoContenuto)) { return SerieTv.builder()... }

        return null;
    }
}