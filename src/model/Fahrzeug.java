package model;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ein Fahrzeug der Tabelle {@code fahrzeug} samt der zugehörigen Fachlogik:
 * Registrieren, Löschen und Prüfen des Kennzeichens.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Fahrzeug
{


    private MyConnection myCon;
    private PropertyChangeHandle pch;

    private String nummernschild, typ;

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


    public Fahrzeug() {}

    public Fahrzeug(String nummernschild, String typ)
    {
        this.nummernschild = nummernschild;
        this.typ = typ;
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
     * Trägt ein Fahrzeug in die Datenbank ein.
     *
     * <p>Steht das Kennzeichen bereits in der Tabelle, wird nichts eingefügt und
     * die View über "Vorhanden" benachrichtigt.
     *
     * @param nummernschild das Kennzeichen; wird in Großbuchstaben gespeichert
     * @param typ           Fahrzeugtyp, zum Beispiel "Auto"
     * @param admin         {@code true} für den Aufruf aus dem Adminbereich, wo der
     *                      Benutzer eine Bestätigung erwartet; bei der Einfahrt
     *                      wird still registriert
     * @return {@code true}, wenn das Fahrzeug neu eingetragen wurde
     */
    public boolean fahrzeugRegistrieren(String nummernschild, String typ, boolean admin)
    {
        boolean retVal = false;
        if (!nummernschild.equals("") && checkNummernschild(nummernschild))
        {
            String[] pos = new String[2];
            pos[0] = nummernschild.toUpperCase();
            pos[1] = typ;
            int insert =0;
            insert =myCon.executeUpdate("INSERT INTO parkhaus.fahrzeug (nummernschild, typ) VALUES ('" + pos[0] + "','" + pos[1] + "');");

            if(insert !=0)
            {
                retVal =true;
                if(admin)
                    pch.propertyChange("Regist", pos);
            }
            else
            {
                pch.propertyChange("Vorhanden",pos);
            }


        }
        return retVal;
    }

    /**
     * Prüft das Kennzeichen gegen das deutsche Format: ein bis drei Buchstaben,
     * Bindestrich, ein bis zwei Buchstaben, Leerzeichen, zwei bis fünf Ziffern ohne
     * führende Null. Passt es nicht, geht "FailCheck" an die View.
     *
     * @param nummernschild die Eingabe des Benutzers; Groß- und Kleinschreibung
     *                      spielt keine Rolle
     * @return {@code true}, wenn das Format stimmt
     */
    public boolean checkNummernschild(String nummernschild)
    {
        boolean retVal = false;

        Pattern p = Pattern.compile("[A-Z]{1,3}-[A-Z]{1,2}\s[1-9][0-9]{1,4}");
        Matcher m = p.matcher(nummernschild.toUpperCase());

        if (m.matches())
            retVal = true;
        else
            pch.propertyChange("FailCheck", nummernschild);

        return retVal;
    }

    /**
     * Löscht ein Fahrzeug aus der Tabelle, sofern es nicht gerade im Parkhaus steht.
     *
     * <p>Ein geparktes Fahrzeug wird bewusst nicht gelöscht — sein Parkplatz würde
     * mitverschwinden — und meldet stattdessen "Verboten" an die View. Die
     * Datenbank verhindert denselben Fall über {@code ON DELETE RESTRICT}.
     *
     * @param nummernschild Kennzeichen des zu löschenden Fahrzeugs
     * @param typ           Fahrzeugtyp; wird nur für die Rückmeldung gebraucht
     */
    public void loescheFahrzeug(String nummernschild, String typ)
    {
            if (!nummernschild.equals("") && checkNummernschild(nummernschild))
            {
                String[] pos = new String[2];
                pos[0] = nummernschild.toUpperCase();
                pos[1] = typ;


                List<Integer> fahrzeug = myCon.queryList
                (
              "Select Parketage_etageNr, platzNr, typ " +
                    "from garage join fahrzeug " +
                    "on Fahrzeug_nummernschild = nummernschild " +
                    "where Fahrzeug_nummernschild = '" + nummernschild + "';",
            rs->rs.getInt("Parketage_etageNr")
                );

                if(fahrzeug.isEmpty())
                {
                    int delete=0;
                    delete = myCon.executeUpdate("DELETE FROM fahrzeug WHERE (nummernschild = '" + pos[0] + "');");
                    if(delete !=0)
                        pch.propertyChange("Loeschen", pos);
                    else
                        pch.propertyChange("LoeschenFail", pos);
                }
                else
                {
                    pch.propertyChange("Verboten",nummernschild);
                }

            }
    }

    /**
     * Liest alle registrierten Fahrzeuge und schickt sie als Liste an die View, die
     * daraus ihre Tabelle füllt.
     */
    public void autoTabelle()
    {
        List<Fahrzeug> liste_Fahrzeug = myCon.queryList
        (
                "Select * from parkhaus.fahrzeug;",
                rs -> new Fahrzeug
                (
                    rs.getString("nummernschild"),
                    rs.getString("typ")
                )
        );

        pch.propertyChange("AutoTab", liste_Fahrzeug);
        pch.propertyChange("PanelTabelleAuto", null);
    }



}
