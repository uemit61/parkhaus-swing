package model.entity;

/**
 * Ein Fahrzeug der Tabelle {@code fahrzeug}: Kennzeichen und Typ.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen: JPA verlangt ihn,
 * falls das Projekt später auf Spring Boot umzieht.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Fahrzeug
{
    // Felder der Tabelle in der DB
    private String nummernschild;
    private String typ;

    // Getter und Setter
    public String getNummernschild()
    {
        return nummernschild;
    }

    public void setNummernschild(String nummernschild)
    {
        this.nummernschild = nummernschild;
    }

    public String getTyp()
    {
        return typ;
    }

    public void setTyp(String typ)
    {
        this.typ = typ;
    }

    //Konstruktoren
    public Fahrzeug(){}; //Default-Konstruktor
    public Fahrzeug(String nummernschild, String typ)
    {
        this.nummernschild = nummernschild;
        this.typ = typ;
    }
}
