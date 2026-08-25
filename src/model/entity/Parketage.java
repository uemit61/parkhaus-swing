package model.entity;

/**
 * Eine Etage der Tabelle {@code parketage} mit ihrer Anzahl an Stellplätzen.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Aus den Kapazitäten aller Etagen leitet der Service sowohl die
 * Gesamtzahl der Plätze als auch die Etage einer Platznummer ab.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Parketage
{
    // Felder der DB-Tabelle
    private int etageNr;
    private int anzahlPlaetze;

    // Getter und Setter
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

    //Konstruktoren
    public Parketage(){};
    public Parketage(int etageNr, int anzahlPlaetze)
    {
        this.etageNr = etageNr;
        this.anzahlPlaetze = anzahlPlaetze;
    }
}
