package model;


/**
 * Eine Etage der Tabelle {@code parketage} mit ihrer Anzahl an Stellplätzen.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Parketage
{
    private PropertyChangeHandle pch;
    private MyConnection myCon;

    private int etageNr, anzahlPlaetze;

    public int getEtageNr()
    {
        return etageNr;
    }

    public void setEtageNr(int etageNr)
    {
        this.etageNr = etageNr;
    }

    public int getAnzahlPlaetze()
    {
        return anzahlPlaetze;
    }

    public void setAnzahlPlaetze(int anzahlPlaetze)
    {
        this.anzahlPlaetze = anzahlPlaetze;
    }

    public Parketage(){};
    public Parketage(int etageNr, int anzahlPlaetze)
    {
        this.etageNr = etageNr;
        this.anzahlPlaetze = anzahlPlaetze;
    }

    /**
     * Reicht die gemeinsam genutzten Objekte nach, die der Composition Root in
     * {@code MainGarage} erzeugt.
     *
     * @param myCon Zugang zur Datenbank
     * @param pch   Kanal für Meldungen an die View
     */
    public void inits(MyConnection myCon,PropertyChangeHandle pch)
    {
        this.myCon = myCon;
        this.pch = pch;

    }

    /**
     * Berechnet die freien Plätze als Summe aller Etagen-Kapazitäten abzüglich der
     * belegten Plätze und meldet das Ergebnis über "Frei" an die View.
     */
    public void freiePlaetze()
    {
        int  frei= myCon.queryList("Select (select sum(anzahlPlaetze) from parketage ), (select count(*) from garage);",rs->rs.getInt(1) - rs.getInt(2)).getFirst();
        pch.propertyChange("Frei", frei);
    }
}
