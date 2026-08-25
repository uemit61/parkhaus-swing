package model.service;


import model.PropertyChangeHandle;
import model.dao.ParketageDao;

public class ParkplatzService
{

    ParketageDao parketageDao;
    PropertyChangeHandle pch;

    public ParkplatzService(ParketageDao parketageDao, PropertyChangeHandle pch)
    {
        this.parketageDao = parketageDao;
        this.pch = pch;
    }

    public void freiePlaetze()
    {
        int freiePlaetze = parketageDao.zaehleFreiePlaetze();
        pch.propertyChange("Frei", freiePlaetze);
    }
}
