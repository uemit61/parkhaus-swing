package model.entity;

/**
 * Ein belegter Parkplatz der Tabelle {@code garage}: Platznummer, Etage und das
 * Kennzeichen des Fahrzeugs, das dort steht.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen: JPA verlangt ihn,
 * falls das Projekt später auf Spring Boot umzieht.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Garage
{
    // Felder der DB-Tabelle
    private int platzNr;
    private int parketage_etageNr;
    private String fahrzeug_nummernschild;


    // Getter und Setter
    public int getPlatzNr()
    {
        return platzNr;
    }

    public void setPlatzNr(int platzNr)
    {
        this.platzNr = platzNr;
    }

    public int getParketage_etageNr()
    {
        return parketage_etageNr;
    }

    public void setParketage_etageNr(int parketage_etageNr)
    {
        this.parketage_etageNr = parketage_etageNr;
    }

    public String getFahrzeug_nummernschild()
    {
        return fahrzeug_nummernschild;
    }

    public void setFahrzeug_nummernschild(String fahrzeug_nummernschild)
    {
        this.fahrzeug_nummernschild = fahrzeug_nummernschild;
    }

    //Konstruktoren
    public Garage(){}; // Default-konstruktor
    public Garage(int platzNr,String fahrzeug_nummernschild, int parketage_etageNr)
    {
        this.fahrzeug_nummernschild = fahrzeug_nummernschild;
        this.parketage_etageNr = parketage_etageNr;
        this.platzNr = platzNr;
    }
}
