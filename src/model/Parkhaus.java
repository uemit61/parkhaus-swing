package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Parkhaus
{

    private MyConnection myCon;
    private PropertyChangeSupport support;
    private ResultSet rs = null;


    public Parkhaus()
    {
        myCon = new MyConnection("jdbc:mysql://localhost:3307/parkhaus", "root", "root");
        support = new PropertyChangeSupport(this);
    }


    /*
     * Es wird die Anzahl der freien Parkplätze ermittelt,
     * und an die View per propertyChange übergeben.
     */
    public void freiePlaetze()
    {
        // Abfrage der Anzahl der geamten Parkplätze, und der Anzahl an belegten Plätzen
        // Die Differenz siehe Unten ergibt die Anzahl an freien Plätzen.
        rs = myCon.executeQuery("Select (select sum(anzahlPlaetze) from parketage ), (select count(*) from garage);");

        try
        {
            rs.next();
            int frei = 0;
            frei = rs.getInt(1) - rs.getInt(2);
            propertyChange("Frei", frei);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * Neue Fahrzeuge werden in die Datenbank.Fahrzeuge aufgenommen,
     * und bekommen ein Parkplatz zugewiesen.
     * Kennzeichen die sich im Parkhaus befinden werden der Polizei gemeldet.
     */
    public void befahren(String nummernschild, String fahrzeugtyp)
    {
        try
        {
            // Prüfen ob Auto im Parkhaus bereits parkt, wenn ja ...POLIZEI alamieren.
            rs = myCon.executeQuery("Select * from garage where Fahrzeug_nummernschild = '" + nummernschild + "';");

            if (!rs.next() && !nummernschild.equals("") && checkNummernschild(nummernschild))
            {
                fahrzeugRegistrieren(nummernschild, fahrzeugtyp);

                rs = myCon.executeQuery("select platzNr from garage;");

                // Ermittlung der kleinsten freien PlatzNummer
                int i = 1; // i entspricht PlatzNr
                while (rs.next())
                {
                    if (rs.getInt(1) == i)
                        i++;
                    else
                        break;
                }



                int e = 0; // e entspricht EtageNr
                if (i > 20 && i < 121)
                    e = 1;
                else if (i > 120 && i < 221)
                    e = 2;
                else if (i > 220)
                    propertyChange("Voll", null);


                // Parkplatz wird zugewiesen
                myCon.execute("INSERT INTO garage (platzNr, Fahrzeug_nummernschild, Parketage_etageNr) VALUES (" + i + ",'" + nummernschild.toUpperCase() + "'," + e + ");");
                freiePlaetze(); // Aktualisiert die Anzeige der freien Plätze
                zeigePostion(nummernschild);

            }
            else if (!nummernschild.equals("") && checkNummernschild(nummernschild))
            {
                propertyChange("Alarm", null);
            }

        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * Parkplatz wieder frei, Fahrzeug nicht im Parkhaus.
     * Das Fahrzeug wird nicht vom Datenbank.Fahrzeug gelöscht.
     *
     */
    public void verlassen(String nummernschild)
    {
        rs = myCon.executeQuery("Select * from garage where Fahrzeug_nummernschild = '" + nummernschild + "';");

        try
        {
            if (!nummernschild.equals("") && checkNummernschild(nummernschild) && rs.next()) // Auto existiert
            {
                myCon.execute("DELETE FROM parkhaus.garage WHERE (Fahrzeug_nummernschild = '" + nummernschild + "');");
                freiePlaetze();
            }
            else if (!nummernschild.equals("") && checkNummernschild(nummernschild))
                propertyChange("Fail", nummernschild);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void zeigePostion(String nummernschild)
    {
        rs = myCon.executeQuery("Select * from garage where Fahrzeug_nummernschild = '" + nummernschild + "';");
        try
        {
            if (!nummernschild.equals("") && checkNummernschild(nummernschild) && rs.next()) // Auto existiert
            {
                String[] pos = new String[4];

                rs = myCon.executeQuery("Select Parketage_etageNr, platzNr, typ from garage join fahrzeug on Fahrzeug_nummernschild = nummernschild where Fahrzeug_nummernschild = '" + nummernschild + "';");
                rs.next();

                pos[0] = rs.getString(1);
                pos[1] = rs.getString(2);
                pos[2] = rs.getString(3);
                pos[3] = nummernschild;

                propertyChange("ZeigePos", pos);
            }
            else if (!nummernschild.equals("") && checkNummernschild(nummernschild))
                propertyChange("Fail", nummernschild);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void fahrzeugRegistrieren(String nummernschild, String typ)
    {


        if (!nummernschild.equals("") && checkNummernschild(nummernschild))
        {

            // Prüfung ob Fahrzeug schon registriert ist
            rs = myCon.executeQuery("Select * from fahrzeug where nummernschild = '" + nummernschild.toUpperCase() + "';");
            // nein einfügen, ja nichts machen
            try
            {
                if (!rs.next() && !nummernschild.equals(""))
                {
                    String[] pos = new String[2];
                    pos[0] = nummernschild.toUpperCase();
                    pos[1] = typ;
                    myCon.execute("INSERT INTO parkhaus.fahrzeug (nummernschild, typ) VALUES ('" + pos[0] + "','" + pos[1] + "');");
                    propertyChange("Regist", pos);
                }

            }

            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean checkNummernschild(String nummernschild)
    {
        boolean retVal = false;

        Pattern p = Pattern.compile("[A-Z]{1,3}-[A-Z]{1,2}\s[1-9][0-9]{1,4}");
        Matcher m = p.matcher(nummernschild.toUpperCase());

        if (m.matches())
            retVal = true;
        else
            propertyChange("FailCheck", nummernschild);

        return retVal;
    }

    public void loescheFahrzeug(String nummernschild, String typ)
    {

        // nein einfügen
        try
        {
            if (!nummernschild.equals("") && checkNummernschild(nummernschild))
            {
                String[] pos = new String[2];
                pos[0] = nummernschild.toUpperCase();
                pos[1] = typ;

                // Prüfung ob Fahrzeug schon registriert ist
                rs = myCon.executeQuery("Select * from fahrzeug where nummernschild = '" + nummernschild.toUpperCase() + "';");

                if (rs.next())
                {
                    myCon.execute("DELETE FROM fahrzeug WHERE (nummernschild = '" + pos[0] + "');");
                    propertyChange("Loeschen", pos);
                }
                else
                    propertyChange("LoeschenFail", pos);
            }
        }

        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parkplatzTabelle()
    {
        rs = myCon.executeQuery("Select * from garage;");

        propertyChange("TabAn", rs);
        propertyChange("PanelTabelle", null);
    }

    public void autoTabelle()
    {
        rs = myCon.executeQuery("Select * from fahrzeug;");

        propertyChange("AutoTab", rs);
        propertyChange("PanelTabelleAuto", null);
    }

    public void addPropertyChangeListener(String probName, PropertyChangeListener pcl)
    {
        support.addPropertyChangeListener(probName, pcl);
    }

    public void propertyChange(String propName, Object newValue)
    {
        support.firePropertyChange(propName, null, newValue);
    }


}
