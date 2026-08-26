package model.dao;

import model.entity.Fahrzeug;
import model.db.MyConnection;

import java.util.List;

/**
 * Datenzugriff auf die Tabelle {@code fahrzeug}: hält das SQL dieser Tabelle und
 * liefert {@link Fahrzeug}-Objekte zurück.
 *
 * <p>Die Klasse trifft keine fachlichen Entscheidungen und meldet nichts an die
 * View — beides gehört in {@code model.service}.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class FahrzeugDao
{
    private final MyConnection myCon;

    /**
     * @param myCon Zugang zur Datenbank; wird vom Composition Root gereicht
     */
    public FahrzeugDao(MyConnection myCon)
    {
        this.myCon = myCon;
    }

    //Create

    /**
     * Trägt ein Fahrzeug ein.
     *
     * @param fahrzeug das einzutragende Fahrzeug; Kennzeichen und Typ werden
     *                 gespeichert
     * @return {@code true}, wenn eine Zeile geschrieben wurde; {@code false}, wenn
     *         das Kennzeichen bereits vorhanden ist — es ist der Primärschlüssel
     */
    public boolean insert(Fahrzeug fahrzeug)
    {
        boolean retVal =false;
        int insert;


        insert= myCon.executeUpdate("INSERT INTO parkhaus.fahrzeug (nummernschild, typ) VALUES (?,?)",fahrzeug.getNummernschild(),fahrzeug.getTyp());
        if(insert !=0)
            retVal =true;

        return retVal;
    }

    //Read

    /**
     * Liest alle registrierten Fahrzeuge.
     *
     * @return alle Zeilen der Tabelle; eine leere Liste, wenn keine da sind oder
     *         die Abfrage fehlgeschlagen ist
     */
    public List<Fahrzeug> findAll()
    {
        return myCon.queryList
        (
    "Select * from parkhaus.fahrzeug;",
  rs -> new Fahrzeug
            (
                rs.getString("nummernschild"),
                rs.getString("typ")
            )
        );
    }

    /**
     * Prüft, ob ein Kennzeichen bereits registriert ist.
     *
     * <p>Die Abfrage liest bewusst nur {@code 1} statt ganzer Zeilen — gefragt ist
     * allein, ob es einen Treffer gibt.
     *
     * @param nummernschild das gesuchte Kennzeichen
     * @return {@code true}, wenn das Fahrzeug in der Tabelle steht
     */
    public boolean existsByNummernschild(String nummernschild)
    {

        return !myCon.queryList("Select 1 from parkhaus.fahrzeug where nummernschild = ?",
                rs -> 1, nummernschild).isEmpty();
    }

    /**
     * Liest den Fahrzeugtyp, der zu einem Kennzeichen registriert ist.
     *
     * <p>Der Service gleicht ihn mit dem Typ ab, den der Benutzer an der Oberfläche
     * gewählt hat: weichen sie voneinander ab, ist eines der beiden Kennzeichen
     * gefälscht.
     *
     * <p>Liefert die Abfrage nichts — weil das Kennzeichen nicht registriert ist
     * oder die Datenbank nicht erreichbar war —, kommt {@code "err"} zurück. Dieser
     * Wert stimmt mit keinem Fahrzeugtyp überein und führt beim Aufrufer deshalb
     * zum Alarm; ein Ausfall der Datenbank sieht damit aus wie ein gefälschtes
     * Kennzeichen.
     *
     * @param nummernschild das gesuchte Kennzeichen
     * @return der registrierte Fahrzeugtyp, oder {@code "err"}, wenn keine Zeile
     *         gelesen werden konnte
     */
    public String checkTypBy(String nummernschild)
    {
        String retVal = "err";
        List<String> liste = myCon.queryList("SELECT typ  from parkhaus.fahrzeug where nummernschild = ? ",rs->rs.getString("typ"),nummernschild);

        if(!liste.isEmpty())
           retVal= liste.getFirst();

        return retVal;
    }
    //Update
        //wird nicht benötigt

    //Delete

    /**
     * Löscht ein Fahrzeug.
     *
     * <p>Kennzeichen und Typ müssen beide passen — ein Löschversuch mit dem
     * falschen Typ greift bewusst nicht.
     *
     * @param fahrzeug das zu löschende Fahrzeug
     * @return {@code true}, wenn eine Zeile gelöscht wurde; {@code false}, wenn
     *         nichts passte oder die Datenbank das Löschen verweigert hat, weil
     *         das Fahrzeug noch einen Parkplatz belegt ({@code ON DELETE RESTRICT})
     */
    public boolean delete(Fahrzeug fahrzeug)
    {
        boolean retVal = false;
        int delete;

        delete =myCon.executeUpdate("DELETE FROM parkhaus.fahrzeug WHERE nummernschild = ? AND typ = ?", fahrzeug.getNummernschild(),fahrzeug.getTyp());
        if(delete !=0)
            retVal =true;

        return retVal;
    }



}
