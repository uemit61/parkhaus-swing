package model;



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

    public void inits(MyConnection myCon,PropertyChangeHandle pch)
    {
        this.myCon = myCon;
        this.pch = pch;

    }

    public void freiePlaetze()
    {
        int  frei= myCon.queryList("Select (select sum(anzahlPlaetze) from parketage ), (select count(*) from garage);",rs->rs.getInt(1) - rs.getInt(2)).getFirst();
        pch.propertyChange("Frei", frei);
    }
}
