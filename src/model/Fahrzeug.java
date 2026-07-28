package model;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void inits(MyConnection myCon,PropertyChangeHandle pch)
    {
        this.myCon = myCon;
        this.pch = pch;
    }

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
