package model.service;


import model.entity.Garage;
import model.entity.Parketage;
import model.event.PropertyChangeHandle;

import model.dao.GarageDao;
import model.dao.ParketageDao;

import java.util.List;

/**
 * Fachlogik rund um die belegten Parkplätze: Ein- und Ausfahrt, Positionsabfrage
 * und die Tabelle der belegten Plätze.
 *
 * <p>Holt die Daten über die DAOs und meldet Ergebnisse über den Ereignis-Kanal
 * an die View; SQL steht hier keines. Für die Kennzeichenprüfung und das
 * Registrieren nutzt die Klasse den {@link FahrzeugService}, statt beides zu
 * wiederholen.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class GarageService
{
    private final GarageDao garageDao;
    private final ParketageDao parketageDao;
    private final PropertyChangeHandle pch;
    private final FahrzeugService fahrzeugService;


    /**
     * @param fahrzeugService liefert Kennzeichenprüfung und Registrierung
     * @param pch             Kanal für Meldungen an die View
     * @param parketageDao    Zugriff auf die Tabelle {@code parketage}; liefert die
     *                        Kapazitäten für die Etagenzuordnung
     * @param garageDao       Zugriff auf die Tabelle {@code garage}
     */
    public GarageService(FahrzeugService fahrzeugService, PropertyChangeHandle pch, ParketageDao parketageDao, GarageDao garageDao)
    {
        this.pch = pch;
        this.parketageDao = parketageDao;
        this.garageDao = garageDao;
        this.fahrzeugService = fahrzeugService;
    }

    /**
     * Lässt ein Fahrzeug einfahren: prüft das Kennzeichen, registriert das Fahrzeug,
     * falls nötig, sucht den kleinsten freien Platz und weist ihn zu.
     *
     * <p>Die Etage steht nicht im Code: die Kapazitäten aus {@code parketage} werden
     * der Reihe nach aufsummiert, bis die gesuchte Platznummer hineinfällt. Reicht
     * die Summe aller Etagen nicht, ist das Parkhaus voll und es wird nichts
     * eingetragen.
     *
     * @param nummernschild Kennzeichen des einfahrenden Fahrzeugs
     * @param typ           Fahrzeugtyp aus der Auswahlliste der Oberfläche
     */
    public void befahren(String nummernschild, String typ)
    {
        if (FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {
            if (garageDao.existsByNummernschild(nummernschild))
            {
                pch.propertyChange("Alarm",null);
            }
            else
            {
                fahrzeugService.fahrzeugRegistrieren(nummernschild,typ,false);

                List<Integer> platzNrListe = garageDao.findAllPlatzNr();
                // Die Liste der Platznummern wird durchlaufen, die erste freie Platznummer, wird ausgewählt.
                int i = 1; // i entspricht PlatzNr
                for (Integer value : platzNrListe)
                {
                    if (value == i)
                        i++;
                    else
                        break;
                }

                List<Parketage> parketageListe = parketageDao.findAllOrderByEtageNr();

                int j=0;
                int etageNr =0;

                // Um die EtagenNr festzustellen, wird die Anzahl der Plätze pro etage durchlaufen, summiert und mit der PlatzNr
                // verglichen, da die PlatzNr unabhängig von der Etage inkrementiert werden.
                for(Parketage e: parketageListe)
                {
                    j=j+e.getAnzahlPlaetze();
                    if(i<=j)
                    {
                        etageNr= e.getEtageNr();
                        break;
                    }
                }


                Garage garage = new Garage(i,nummernschild.toUpperCase(),etageNr);
                if(i>j)
                    pch.propertyChange("Voll", null);
                else if(garageDao.insert(garage))
                {
                    String[] viewInfo = new String[]{typ, nummernschild, "" + etageNr, "" + i};
                    pch.propertyChange("ZeigePos", viewInfo);
                }
            }


        }
        else
        {
            pch.propertyChange("FailCheck", nummernschild);
        }


    }

    /**
     * Bucht ein Fahrzeug aus und gibt seinen Parkplatz frei. Ob das Fahrzeug
     * überhaupt im Parkhaus stand, verrät die Anzahl der gelöschten Zeilen — eine
     * eigene Abfrage vorher erübrigt sich dadurch.
     *
     * @param nummernschild Kennzeichen des ausfahrenden Fahrzeugs
     */
    public void verlassen(String nummernschild)
    {
        if(FahrzeugService.istGueltigesKennzeichen( nummernschild))
        {
            if(garageDao.deleteByNummernschild(nummernschild))
                pch.propertyChange("Verlassen", nummernschild);
            else
                pch.propertyChange("Fail", nummernschild);
        }
        else
        {
            pch.propertyChange("FailCheck", nummernschild);
        }

    }

    /**
     * Sucht Etage und Platznummer eines Fahrzeugs und meldet beides über "ZeigePos"
     * an die View. Steht das Fahrzeug nicht im Parkhaus, geht stattdessen "Fail"
     * hinaus.
     *
     * <p>Die Werte gehen in derselben Reihenfolge hinaus wie bei {@code befahren},
     *    das dasselbe Ereignis sendet: Typ, Kennzeichen, Etage, Platz.
     *
     * @param nummernschild das gesuchte Kennzeichen
     */
    public void zeigePostion(String nummernschild)
    {
        if(FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {
            List<String[]> position =garageDao.findPositionByNummernschild(nummernschild);

            if (!position.isEmpty())
                pch.propertyChange("ZeigePos", position.getFirst());
            else
                pch.propertyChange("Fail", nummernschild);
        }
        else
        {
            pch.propertyChange("FailCheck", nummernschild);
        }
    }

    /**
     * Liest alle belegten Parkplätze und schickt sie als Liste an die View, die
     * daraus ihre Tabelle füllt.
     */
    public void parkplatzTabelle()
    {
        List<Garage> garageListe =garageDao.findAll();

        pch.propertyChange("TabAn", garageListe);
        pch.propertyChange("PanelTabelle", null);
    }

}
