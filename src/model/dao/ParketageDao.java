package model.dao;

import model.entity.Parketage;
import model.db.MyConnection;

import java.util.List;

/**
 * Datenzugriff auf die Tabelle {@code parketage}, die Etagen mit ihren
 * Kapazitäten.
 *
 * <p>Die Klasse trifft keine fachlichen Entscheidungen und meldet nichts an die
 * View — beides gehört in {@code model.service}.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class ParketageDao
{
    private final MyConnection myCon;

    /**
     * @param myCon Zugang zur Datenbank; wird vom Composition Root gereicht
     */
    public ParketageDao(MyConnection myCon)
    {
        this.myCon = myCon;
    }

    //Read

    /**
     * Liest alle Etagen aufsteigend nach Etagennummer.
     *
     * <p>Die Reihenfolge ist wesentlich: der Service summiert die Kapazitäten in
     * genau dieser Folge auf, um einer Platznummer ihre Etage zuzuordnen.
     *
     * @return alle Etagen, aufsteigend sortiert; eine leere Liste, wenn keine
     *         Stammdaten hinterlegt sind
     */
    public List<Parketage> findAllOrderByEtageNr()
    {
        return myCon.queryList("Select * from parketage order by etageNr",rs->new Parketage(rs.getInt("etageNr"),rs.getInt("anzahlPlaetze")));
    }
    /**
     * Zählt die freien Plätze als Summe aller Etagen-Kapazitäten abzüglich der
     * belegten Plätze.
     *
     * @return Anzahl der freien Plätze; {@code -1}, wenn die Abfrage nichts
     *         geliefert hat — etwa weil die Datenbank nicht erreichbar war
     */
    public int zaehleFreiePlaetze()
    {
        int retVal = -1;

        List<Integer> liste =myCon.queryList("Select (select sum(anzahlPlaetze) from parketage ), (select count(*) from garage);",rs->rs.getInt(1) - rs.getInt(2));

        if(!liste.isEmpty())
        {
            retVal= liste.getFirst();
        }

        return retVal;
    }
}
