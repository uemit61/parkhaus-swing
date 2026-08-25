package model.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kapselt den Zugriff auf die MySQL-Datenbank. Jede Methode öffnet ihre eigene
 * Verbindung und schließt sie über try-with-resources wieder; die Klasse hält
 * deshalb keinen Verbindungszustand.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class MyConnection
{
    private String url, username, password;

    /**
     * Merkt sich die Zugangsdaten, mit denen später jede Verbindung aufgebaut wird.
     * Die Werte stammen aus der {@code config.properties}, die {@code MainGarage}
     * beim Start einliest.
     *
     * @param url      JDBC-URL der Datenbank, zum Beispiel
     *                 {@code jdbc:mysql://localhost:3306/parkhaus}
     * @param username Benutzername für die Anmeldung
     * @param password Passwort für die Anmeldung
     */
    public MyConnection(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Baut eine neue Verbindung zur Datenbank auf.
     *
     * @return die geöffnete Verbindung
     * @throws RuntimeException wenn keine Verbindung aufgebaut werden kann, etwa
     *         weil der Server nicht läuft oder die Zugangsdaten nicht stimmen
     */
    public Connection getConnection()
    {
        try
        {
            return DriverManager.getConnection(url,username,password);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Es konnte keine Verbindung hergestellt werden."+e.getMessage(),e);
        }
    }

    /**
     * Führt dasselbe INSERT, UPDATE oder DELETE für viele Datensätze aus.
     *
     * <p>Die Zeilen werden gesammelt und in einem Rutsch zur Datenbank geschickt,
     * statt für jede einzeln hin und her zu gehen. Sie laufen in einer
     * gemeinsamen Transaktion: scheitert eine Zeile, wird keine geschrieben.
     *
     * <p>Für einen einzelnen Datensatz ist {@link #executeUpdate(String, Object...)}
     * die einfachere Wahl.
     *
     * @param query      SQL-Statement mit {@code ?} als Platzhalter, zum Beispiel
     *                   {@code INSERT INTO fahrzeug (nummernschild, typ) VALUES (?,?)}
     * @param paramsList je Eintrag ein Array mit den Werten einer Zeile, in der
     *                   Reihenfolge der Platzhalter
     * @return je Zeile die Anzahl der von ihr geänderten Datensätze; ein leeres
     *         Array, wenn der Stapel fehlgeschlagen ist und zurückgerollt wurde.
     *         Einzelne Einträge können {@code Statement.SUCCESS_NO_INFO} sein,
     *         wenn der Treiber die genaue Zahl nicht meldet
     */
    public int[] executeBatch(String query, List<Object[]> paramsList)
    {
        int[] retVal = new int[0];
        try (Connection con = getConnection())
        {
            con.setAutoCommit(false);

            try(PreparedStatement st = con.prepareStatement(query))
            {
                for(Object[] params: paramsList)
                {
                    setParams(st,params);
                    st.addBatch();
                }

                retVal = st.executeBatch();
                con.commit();
            }
            catch (SQLException e)
            {
                System.err.print("Fehler executeBatch(): "+e);
                retVal= new int[0]; // retVal wieder zurücksetzen, weil durch den Abbruch nichts geschrieben wurde
                con.rollback();
                throw e; // Weiterwerfen, damit der äußere Block Bescheid weiß
            }
        }
        catch(SQLException e)
        {
            // Technische Ursache ausgeben. Fachlich deuten können nur die Modelle:
            // Ein doppeltes Kennzeichen meldet Fahrzeug als "Vorhanden" an die View.
            System.out.println("Update fehlgeschlagen: " + e.getMessage());
        }

        return retVal;
    }

    /**
     * Führt ein INSERT, UPDATE oder DELETE für einen einzelnen Datensatz aus.
     *
     * <p>Die Werte stehen nicht im SQL-Text, sondern werden als Platzhalter
     * gesetzt — das schließt SQL-Injection aus und erspart das Quoting je nach
     * Datentyp.
     *
     * <p>Scheitert das Statement, wird zurückgerollt und 0 gemeldet. Die
     * Rückgabezahl ist damit die einzige Rückmeldung an die Modelle: dass ein
     * Kennzeichen schon vorhanden ist, erkennt {@code Fahrzeug} daran, dass 0
     * zurückkommt.
     *
     * @param query  SQL-Statement mit {@code ?} als Platzhalter, zum Beispiel
     *               {@code DELETE FROM fahrzeug WHERE nummernschild = ?}
     * @param params die Werte für die Platzhalter in ihrer Reihenfolge, typgerecht
     *               übergeben — eine Platznummer als {@code int}, nicht als Text
     * @return Anzahl der geänderten Zeilen; 0, wenn nichts geändert wurde oder das
     *         Statement fehlgeschlagen ist
     */
    public int executeUpdate(String query,Object... params)
    {
        int retVal=0;
        try (Connection con = getConnection())
        {
            con.setAutoCommit(false);

            try(PreparedStatement st = con.prepareStatement(query))
            {
                setParams(st,params);
                retVal = st.executeUpdate();
                con.commit();
            }

            catch (SQLException e)
            {
                System.err.print("Fehler executeUpdate(): "+e);
                retVal=0; // retVal wieder zurücksetzen, weil durch den Abbruch nichts geschrieben wurde
                con.rollback();
                throw e; // Weiterwerfen, damit der äußere Block Bescheid weiß
            }
        }
        catch (SQLException e)
        {
            // Technische Ursache ausgeben. Fachlich deuten können nur die Modelle:
            // Ein doppeltes Kennzeichen meldet Fahrzeug als "Vorhanden" an die View.
            System.out.println("Update fehlgeschlagen: " + e.getMessage());
        }
        return retVal;
    }


    /**
     * Führt eine Abfrage aus und wandelt jede Ergebniszeile mit dem übergebenen
     * Mapper in ein Objekt um.
     *
     * <p>Das ResultSet wird vollständig innerhalb dieser Methode verarbeitet und
     * geschlossen — es verlässt die Model-Schicht nicht.
     *
     * @param <T>    Typ der Objekte, die der Mapper erzeugt
     * @param query  SQL-SELECT, Werte als {@code ?} eingesetzt, zum Beispiel
     *               {@code Select * from garage where Fahrzeug_nummernschild = ?}
     * @param mapper wandelt eine einzelne Zeile in ein Objekt um
     * @param params die Werte für die Platzhalter in ihrer Reihenfolge; bei einer
     *               Abfrage ohne Platzhalter einfach weglassen
     * @return Liste der umgewandelten Zeilen; leer, wenn die Abfrage nichts
     *         geliefert hat oder fehlgeschlagen ist
     */
    public <T> List<T> queryList(String query, RowMapper<T> mapper,Object... params)
    {
        List<T> retVal = new ArrayList<>();
        try(Connection con= getConnection();PreparedStatement st = con.prepareStatement(query))
        {
            if(params !=null)
                setParams(st, params);

            ResultSet rs = st.executeQuery();
            while(rs.next())
            {
                retVal.add(mapper.mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            System.out.println("Fehler beim ausführen von queryList(...): "+e);
        }
        return retVal;
    }

    /**
     * Setzt die Werte in die Platzhalter des Statements ein.
     *
     * <p>{@code setObject} erkennt den Datentyp am übergebenen Objekt selbst: ein
     * {@code Integer} geht als Zahl zur Datenbank, ein {@code String} als Text.
     * Die Aufrufer müssen den Typ deshalb nicht mitliefern, und ein Text, der wie
     * eine Zahl aussieht, bleibt Text — eine Postleitzahl wie "01067" behält ihre
     * führende Null.
     *
     * <p>JDBC zählt die Platzhalter ab 1, das Array ab 0; der Zähler läuft deshalb
     * versetzt mit.
     *
     * @param st     das vorbereitete Statement, dessen Platzhalter gefüllt werden
     * @param params die Werte in der Reihenfolge der Platzhalter
     * @throws SQLException wenn ein Wert nicht gesetzt werden kann, etwa weil mehr
     *         Werte übergeben wurden als das Statement Platzhalter hat
     */
    private void setParams(PreparedStatement st,Object... params) throws SQLException
    {
        for (int i = 0; i < params.length; i++)
        {
            st.setObject(i+1, params[i]);
        }
    }

}

