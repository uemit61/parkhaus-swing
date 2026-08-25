package model.service;


import model.event.PropertyChangeHandle;
import model.dao.ParketageDao;

/**
 * Fachlogik rund um die Etagen des Parkhauses.
 *
 * <p>Holt die Daten über {@link ParketageDao} und meldet Ergebnisse über den
 * Ereignis-Kanal an die View; SQL steht hier keines.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class ParketageService
{

    private final ParketageDao parketageDao;
    private final PropertyChangeHandle pch;

    /**
     * @param parketageDao Zugriff auf die Tabelle {@code parketage}
     * @param pch          Kanal für Meldungen an die View
     */
    public ParketageService(ParketageDao parketageDao, PropertyChangeHandle pch)
    {
        this.parketageDao = parketageDao;
        this.pch = pch;
    }

    /**
     * Ermittelt die Anzahl der freien Plätze und meldet sie über "Frei" an die
     * View.
     */
    public void freiePlaetze()
    {
        int freiePlaetze =parketageDao.zaehleFreiePlaetze();

        if(freiePlaetze == -1)
            freiePlaetze =0;

        pch.propertyChange("Frei", freiePlaetze);
    }
}
