package model;

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
            throw new RuntimeException("Es konnte keine Verbindung hergestellt werden."+e.getMessage());
        }
    }

    /**
     * Führt ein INSERT, UPDATE oder DELETE aus.
     *
     * <p>Die Werte stehen nicht im SQL-Text, sondern werden als Platzhalter
     * gesetzt — das schließt SQL-Injection aus und erspart das Quoting je nach
     * Datentyp.
     *
     * <p>Die Methode nimmt bewusst mehrere Parameterzeilen entgegen, damit die
     * Klasse auch außerhalb dieses Projekts für Massenänderungen taugt.
     * Committet wird erst, wenn alle Zeilen durchgelaufen sind.
     *
     * @param query      SQL-Statement mit {@code ?} als Platzhalter, zum Beispiel
     *                   {@code INSERT INTO fahrzeug (nummernschild, typ) VALUES (?,?)}
     * @param paramsList je Zeile ein Array mit den Werten in der Reihenfolge der
     *                   Platzhalter; für einen einzelnen Datensatz ein Array der
     *                   Länge 1
     * @return Anzahl der geschriebenen Zeilen; 0, wenn nichts geändert wurde oder
     *         das Statement fehlgeschlagen ist
     */
    public int executeUpdate(String query,String[][] paramsList)
    {
        int retVal=0;
        try (Connection con = getConnection())
        {
            con.setAutoCommit(false);

            try(PreparedStatement st = con.prepareStatement(query))
            {
                for(String[] params: paramsList)
                {
                    setPreparedStatments(params,st);
                    if(st.executeUpdate() !=0)
                        retVal++;
                }
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
        catch(SQLException e)
        {
            // Technische Ursache ausgeben. Fachlich deuten können nur die Modelle:
            // Ein doppeltes Kennzeichen meldet Fahrzeug als "Vorhanden" an die View.
            System.out.println("Update fehlgeschlagen: " + e.getMessage());
        };

        return retVal;
    }

    public int executeUpdate(String query,Object... params)
    {

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
     * @param params die Werte für die Platzhalter in ihrer Reihenfolge, oder
     *               {@code null}, wenn die Abfrage ohne Platzhalter auskommt
     * @return Liste der umgewandelten Zeilen; leer, wenn die Abfrage nichts
     *         geliefert hat oder fehlgeschlagen ist
     */
    public <T> List<T> queryList(String query,RowMapper<T> mapper,String[] params)
    {
        List<T> retVal = new ArrayList<>();
        try(Connection con= getConnection();PreparedStatement st = con.prepareStatement(query))
        {
                if(params !=null)
                    setPreparedStatments(params,st);

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
     * Setzt die Werte in die Platzhalter des Statements ein. Lässt sich ein Wert
     * als ganze Zahl lesen, wird er als Zahl gesetzt, sonst als Text — die
     * Aufrufer müssen den Datentyp dadurch nicht selbst mitliefern.
     *
     * <p>JDBC zählt die Platzhalter ab 1, das Array ab 0; der Zähler läuft
     * deshalb versetzt mit.
     *
     * <p>Die Typerkennung ist eine Annahme, keine Angabe: ein Text, der wie eine
     * Zahl aussieht, verliert seine führenden Nullen. Für dieses Projekt
     * unkritisch, bei Wiederverwendung zu beachten.
     *
     * @param params die Werte in der Reihenfolge der Platzhalter
     * @param st     das vorbereitete Statement, dessen Platzhalter gefüllt werden
     * @throws SQLException wenn ein Wert nicht gesetzt werden kann, etwa weil mehr
     *         Werte übergeben wurden als das Statement Platzhalter hat
     */
    private void setPreparedStatments(String[] params, PreparedStatement st) throws SQLException
    {
        int i=1;

        int number;
        for(String param: params)
        {
            //Es wird geprüft was für ein Typ der String beinhaltet und dementsprechend wird geparst,
            // und typgerecht eingefügt
            try // prüfung ob eine Zahl int ist
            {
                number=Integer.parseInt(param);
                st.setInt(i,number);
            }
            catch (NumberFormatException e)
            {
                st.setString(i,param);
            }
            i++;
        }
    }

}


