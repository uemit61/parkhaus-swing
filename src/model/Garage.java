package model;




import java.util.List;


public class Garage
{

    private PropertyChangeHandle pch;
    private MyConnection myCon;

    private Fahrzeug fahrzeug = null;

    //Attributes
    private int platzNr;
    private String parketage_etageNr, fahrzeug_nummernschild;

    //Getter und Setter
    public int getPlatzNr()
    {
        return platzNr;
    }

    public void setPlatzNr(int platzNr)
    {
        this.platzNr = platzNr;
    }

    public String getParketage_etageNr()
    {
        return parketage_etageNr;
    }

    public void setParketage_etageNr(String parketage_etageNr)
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

    public Garage() {}

    public Garage(int platzNr, String parketage_etageNr, String fahrzeug_nummernschild)
    {
        this.platzNr = platzNr;
        this.parketage_etageNr = parketage_etageNr;
        this.fahrzeug_nummernschild = fahrzeug_nummernschild;
    }

    public void inits(MyConnection myCon, PropertyChangeHandle pch, Fahrzeug fahrzeug)
    {
        this.myCon = myCon;
        this.pch = pch;
        this.fahrzeug = fahrzeug;
    }


    /**
     * Handelt es sich um ein richtiges Kennzeichen, wird geprüft, ob sich das
     * Fahrzeug im Parkhaus befindet.
     *
     * @param nummernschild Das Kennzeichen
     * @return boolean Falls nicht im Parkhaus wird true zurückgegeben.
     */
    public boolean check(String nummernschild)
    {
        boolean retVal;


        // Prüfen ob Auto im Parkhaus bereits parkt, wenn ja ...POLIZEI alamieren.
        retVal= myCon.queryList("Select * from garage where Fahrzeug_nummernschild = '" + nummernschild + "';",rs->rs.getInt(1)).isEmpty();

        if(!retVal)
            pch.propertyChange("Alarm",null);
        return retVal;
    }
    public void befahren(String nummernschild, String typ)
    {

        if(fahrzeug.checkNummernschild(nummernschild) &&check(nummernschild))
        {
            boolean unbekannt = myCon.queryList(
                    "Select nummernschild from fahrzeug where nummernschild = '" + nummernschild + "';",
                    rs -> rs.getString("nummernschild")).isEmpty();
            if (unbekannt)
                fahrzeug.fahrzeugRegistrieren(nummernschild,typ,false);

            List<Integer> list = myCon.queryList("Select * from garage order by platzNr", rs -> rs.getInt("platzNr"));

            // Ermittlung der kleinsten freien PlatzNummer
            int i = 1; // i entspricht PlatzNr
            for (Integer value : list)
            {
                if (value == i)
                    i++;
                else
                    break;
            }

            List<Parketage> etagen =myCon.queryList("Select * from parketage order by etageNr",rs->new Parketage(rs.getInt("etageNr"),rs.getInt("anzahlPlaetze")));
            int j=0;

            int etageNr =0;
            for(Parketage e: etagen)
            {
                j=j+e.getAnzahlPlaetze();
                if(i<=j)
                {
                    etageNr= e.getEtageNr();
                    break;
                }
            }

            if(i>j)
                pch.propertyChange("Voll", null);
            else
            {
                // Parkplatz wird zugewiesen
                myCon.executeUpdate("INSERT INTO garage (platzNr, Fahrzeug_nummernschild, Parketage_etageNr) VALUES (" + i + ",'" + nummernschild.toUpperCase() + "'," +etageNr  + ");");
                zeigePostion(nummernschild);
            }

        }

    }



    public void verlassen(String nummernschild)
    {
        if(fahrzeug.checkNummernschild(nummernschild))
        {
            int count = myCon.executeUpdate("DELETE FROM parkhaus.garage WHERE (Fahrzeug_nummernschild = '" + nummernschild + "');");

            if(count !=0)
                pch.propertyChange("Verlassen", nummernschild);
            else
                pch.propertyChange("Fail", nummernschild);
        }

    }

    public void zeigePostion(String nummernschild)
    {
        if (fahrzeug.checkNummernschild(nummernschild))
        {
            List<String[]> pos = myCon.queryList
                                (
                              "Select Parketage_etageNr, platzNr, typ " +
                                    "from garage join fahrzeug " +
                                     "on Fahrzeug_nummernschild = nummernschild " +
                                     "where Fahrzeug_nummernschild = '" + nummernschild + "';",
                            rs -> new String[]
                                    {
                                        rs.getString(1),
                                        rs.getString(2), rs.getString(3),
                                        nummernschild
                                    }
                                );
            if (!pos.isEmpty())
                pch.propertyChange("ZeigePos", pos.get(0));
            else
                pch.propertyChange("Fail", nummernschild);
        }

    }

    public void parkplatzTabelle()
    {
        List<Garage> liste_Garage = myCon.queryList
                ("Select * from parkhaus.garage;",
                rs -> new Garage(rs.getInt(
                        "platzNr"),
                        rs.getString("Parketage_etageNr"),
                        rs.getString("Fahrzeug_nummernschild"))
                );
        pch.propertyChange("TabAn", liste_Garage);
        pch.propertyChange("PanelTabelle", null);
    }


}
