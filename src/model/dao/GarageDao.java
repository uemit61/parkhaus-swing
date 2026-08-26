package model.dao;

import model.entity.Fahrzeug;
import model.entity.Garage;
import model.db.MyConnection;

import java.util.List;

/**
 * Datenzugriff auf die Tabelle {@code garage}, die belegten Parkplätze.
 *
 * <p>Die Klasse trifft keine fachlichen Entscheidungen und meldet nichts an die
 * View — beides gehört in {@code model.service}.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class GarageDao
{
    private final MyConnection myCon;

    /**
     * @param myCon Zugang zur Datenbank; wird vom Composition Root gereicht
     */
    public GarageDao(MyConnection myCon)
    {
        this.myCon = myCon;
    }

    //Create

    /**
     * Belegt einen Parkplatz.
     *
     * @param garage der zu belegende Platz: Platznummer, Kennzeichen und Etage
     * @return {@code true}, wenn der Platz eingetragen wurde; {@code false}, wenn
     *         die Platznummer schon vergeben oder das Fahrzeug nicht registriert ist
     */
    public boolean insert(Garage garage)
    {
        boolean retVal =false;
        int insert;

        insert= myCon.executeUpdate("INSERT INTO parkhaus.garage (platzNr, Fahrzeug_nummernschild, Parketage_etageNr) VALUES (?,?,?)",garage.getPlatzNr(),garage.getFahrzeug_nummernschild(),garage.getParketage_etageNr());
        if(insert !=0)
            retVal =true;

        return retVal;
    }



    //Read

    /**
     * Liest alle belegten Parkplätze.
     *
     * @return alle Zeilen der Tabelle; eine leere Liste, wenn nichts belegt ist
     *         oder die Abfrage fehlgeschlagen ist
     */
    public List<Garage> findAll()
    {
        return myCon.queryList
                ("Select * from parkhaus.garage;",
                        rs -> new Garage(
                                rs.getInt("platzNr"),
                                rs.getString("Fahrzeug_nummernschild"),
                                rs.getInt("Parketage_etageNr")
                        )
                );
    }
    /**
     * Prüft, ob ein Fahrzeug gerade im Parkhaus steht.
     *
     * <p>Die Abfrage liest bewusst nur {@code 1} statt ganzer Zeilen — gefragt ist
     * allein, ob es einen Treffer gibt.
     *
     * @param nummernschild das gesuchte Kennzeichen
     * @return {@code true}, wenn das Fahrzeug einen Platz belegt
     */
    public boolean existsByNummernschild(String nummernschild)
    {
        return !myCon.queryList("Select 1 from garage where Fahrzeug_nummernschild = ?",
                rs -> 1, nummernschild).isEmpty();
    }

    /**
     * Liest die belegten Platznummern aufsteigend.
     *
     * <p>Aus der Reihenfolge ermittelt der Service die kleinste freie Nummer: er
     * zählt mit, bis die erste Lücke auftaucht.
     *
     * @return die belegten Platznummern, aufsteigend sortiert
     */
    public List<Integer> findAllPlatzNr()
    {
        return myCon.queryList("Select platzNr from garage order by platzNr", rs -> rs.getInt("platzNr"));
    }
    /**
     * Sucht Etage, Platz und Fahrzeugtyp zu einem Kennzeichen.
     *
     * <p>Der Typ steht in {@code fahrzeug}, Etage und Platz in {@code garage} —
     * daher der Join.
     *
     * @param nummernschild das gesuchte Kennzeichen
     * @return eine Liste mit höchstens einem Eintrag der Form
     *         {@code {typ, nummernschild, etageNr, platzNr}}; leer, wenn das
     *         Fahrzeug nicht im Parkhaus steht. Die Reihenfolge der Werte ist ein
     *         Vertrag mit dem Ereignis "ZeigePos"
     */
    public List<String[]> findPositionByNummernschild(String nummernschild)
    {
        return myCon.queryList
                (
                        "Select Parketage_etageNr, platzNr, typ " +
                                "from garage join fahrzeug " +
                                "on Fahrzeug_nummernschild = nummernschild " +
                                "where Fahrzeug_nummernschild = ?",
                        rs -> new String[]
                                {
                                        rs.getString(3),
                                        nummernschild,
                                        rs.getString(1),
                                        rs.getString(2),

                                },nummernschild
                );
    }

    //Update
        //Nothing to Update

    //Delete

    /**
     * Gibt den Parkplatz eines Fahrzeugs frei, wenn Kennzeichen und Typ zusammen
     * passen.
     *
     * <p>Der Typ steht in {@code fahrzeug}, der Platz in {@code garage} — deshalb
     * löscht die Abfrage über einen Join und prüft beides in einem Schritt.
     *
     * @param nummernschild Kennzeichen des ausfahrenden Fahrzeugs
     * @param typ           der Fahrzeugtyp, der zum Kennzeichen registriert sein muss
     * @return {@code true}, wenn ein Platz freigegeben wurde; {@code false}, wenn
     *         das Fahrzeug nicht im Parkhaus stand oder der Typ nicht passt
     */
    public boolean deleteByNummernschildAndTyp(String nummernschild,String typ)
    {
        boolean retVal = false;
        int count = myCon.executeUpdate
                (
            "DELETE g" +
                    " FROM parkhaus.garage g" +
                    " JOIN parkhaus.fahrzeug f" +
                    " ON f.nummernschild = g.Fahrzeug_nummernschild" +
                    " WHERE f.nummernschild = ? " +
                    " AND f.typ = ?", nummernschild,typ
                );

        if(count !=0)
            retVal = true;

        return retVal;
    }
}
