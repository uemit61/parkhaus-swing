package model.service;

import model.entity.Fahrzeug;
import model.event.PropertyChangeHandle;
import model.dao.FahrzeugDao;
import model.dao.GarageDao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fachlogik rund um die registrierten Fahrzeuge: Registrieren, Löschen und die
 * Prüfung des Kennzeichen-Formats.
 *
 * <p>Holt die Daten über die DAOs und meldet Ergebnisse über den Ereignis-Kanal
 * an die View; SQL steht hier keines. Für das Löschen braucht die Klasse auch
 * den {@link GarageDao} — ein geparktes Fahrzeug darf nicht verschwinden.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class FahrzeugService
{
    private static final Pattern KENNZEICHEN = Pattern.compile("[A-Z]{1,3}-[A-Z]{1,2}\\s[1-9][0-9]{1,4}");
    private final FahrzeugDao fahrzeugDao;
    private final GarageDao garageDao;
    private final PropertyChangeHandle pch;


    /**
     * @param fahrzeugDao Zugriff auf die Tabelle {@code fahrzeug}
     * @param garageDao   Zugriff auf die Tabelle {@code garage}; gebraucht für die
     *                    Prüfung, ob ein Fahrzeug gerade geparkt ist
     * @param pch         Kanal für Meldungen an die View
     */
    public FahrzeugService(FahrzeugDao fahrzeugDao, GarageDao garageDao, PropertyChangeHandle pch)
    {
        this.fahrzeugDao = fahrzeugDao;
        this.garageDao = garageDao;
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
     */
    public void fahrzeugRegistrieren(String nummernschild, String typ, boolean admin)
    {
        String[] props = new String[2];
        props[0] = nummernschild.toUpperCase();
        props[1] = typ;

        if (!nummernschild.isEmpty())
        {
            if (istGueltigesKennzeichen(nummernschild))
            {
                Fahrzeug fahrzeug =new Fahrzeug( props[0],props[1]);
                if (fahrzeugDao.insert(fahrzeug))
                {
                    if (admin)
                        pch.propertyChange("Regist", props);
                }
                else
                    pch.propertyChange("Vorhanden", props);
            }
            else
                pch.propertyChange("FailCheck", nummernschild);
        }
    }

    /**
     * Liest alle registrierten Fahrzeuge und schickt sie als Liste an die View, die
     * daraus ihre Tabelle füllt.
     */
    public void autoTabelle()
    {
        //Datenbank
        List<Fahrzeug> liste_Fahrzeug = fahrzeugDao.findAll();

        //View
        pch.propertyChange("AutoTab", liste_Fahrzeug);
        pch.propertyChange("PanelTabelleAuto", null);
    }

    /**
     * Löscht ein Fahrzeug aus der Tabelle, sofern es nicht gerade im Parkhaus steht.
     *
     * <p>Ein geparktes Fahrzeug wird bewusst nicht gelöscht — sein Parkplatz würde
     * mitverschwinden — und meldet stattdessen "Verboten" an die View. Die
     * Datenbank verhindert denselben Fall über {@code ON DELETE RESTRICT}.
     *
     * @param nummernschild Kennzeichen des zu löschenden Fahrzeugs
     * @param typ           Fahrzeugtyp; steht mit in der WHERE-Bedingung, gelöscht
     *                      wird also nur, wenn Kennzeichen und Typ zusammenpassen
     */
    public void loescheFahrzeug(String nummernschild, String typ)
    {
        if (FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {
            String[] props = new String[2];
            props[0] = nummernschild.toUpperCase();
            props[1] = typ;

            if (!garageDao.existsByNummernschild(nummernschild))
            {
                Fahrzeug fahrzeug =new Fahrzeug(props[0],props[1]);
                if (fahrzeugDao.delete(fahrzeug))
                    pch.propertyChange("Loeschen", props);
                else
                    pch.propertyChange("LoeschenFail", props);
            }
            else
                pch.propertyChange("Verboten", nummernschild);
        }
        else
        {
            pch.propertyChange("FailCheck", nummernschild);
        }

    }

    // sollte eigentlich  unter model/util ein Paket haben, lohnt sich nicht für eine Methode!!!

    /**
     * Prüft das Kennzeichen gegen das deutsche Format: ein bis drei Buchstaben,
     * Bindestrich, ein bis zwei Buchstaben, Leerzeichen, zwei bis fünf Ziffern ohne
     * führende Null (siehe {@code KENNZEICHEN}).
     *
     * <p>Die Methode prüft nur und meldet nichts — "FailCheck" schickt der
     * Aufrufer, der auch weiß, in welchem Zusammenhang die Eingabe kam. Dadurch
     * hat sie keine Abhängigkeiten und lässt sich ohne Datenbank und ohne
     * Oberfläche testen.
     *
     * @param nummernschild die Eingabe des Benutzers; Groß- und Kleinschreibung
     *                      spielt keine Rolle
     * @return {@code true}, wenn das Format stimmt
     */
    public static boolean istGueltigesKennzeichen(String nummernschild)
    {
        boolean retVal = false;

        Matcher m = KENNZEICHEN.matcher(nummernschild.toUpperCase());

        if (m.matches())
            retVal = true;

        return retVal;
    }
}
