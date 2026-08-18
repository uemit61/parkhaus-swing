package model;




import java.util.List;

/**
 * Ein belegter Parkplatz der Tabelle {@code garage} samt der Fachlogik für Ein-
 * und Ausfahrt.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
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

    /**
     * Reicht die gemeinsam genutzten Objekte nach, die der Composition Root in
     * {@code MainGarage} erzeugt.
     *
     * @param myCon    Zugang zur Datenbank
     * @param pch      Kanal für Meldungen an die View
     * @param fahrzeug das Fahrzeug-Model; Garage nutzt es für die Kennzeichen-
     *                 prüfung und die Registrierung, statt beides zu duplizieren
     */
    public void inits(MyConnection myCon, PropertyChangeHandle pch, Fahrzeug fahrzeug)
    {
        this.myCon = myCon;
        this.pch = pch;
        this.fahrzeug = fahrzeug;
    }


    /**
     * Prüft, ob das Fahrzeug noch nicht im Parkhaus steht.
     *
     * <p>Steht dasselbe Kennzeichen bereits drin, kann es kein zweites Mal
     * einfahren — eines von beiden ist gefälscht. In dem Fall geht "Alarm" an die
     * View.
     *
     * @param nummernschild das Kennzeichen
     * @return {@code true}, wenn das Fahrzeug einfahren darf
     */
    public boolean check(String nummernschild)
    {
        boolean retVal;

        String[] conInfo = new String[] {nummernschild};
        // Prüfen ob Auto im Parkhaus bereits parkt, wenn ja ...POLIZEI alamieren.
        retVal= myCon.queryList("Select * from garage where Fahrzeug_nummernschild = ?",rs->rs.getInt(1),conInfo).isEmpty();

        if(!retVal)
            pch.propertyChange("Alarm",null);
        return retVal;
    }

    /**
     * Lässt ein Fahrzeug einfahren: prüft das Kennzeichen, registriert das Fahrzeug
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

        if(fahrzeug.checkNummernschild(nummernschild) &&check(nummernschild))
        {
            String[] conInfo = new String[] {nummernschild};
            boolean unbekannt = myCon.queryList(
                    "Select nummernschild from fahrzeug where nummernschild = ?",
                    rs -> rs.getString("nummernschild"),conInfo).isEmpty();
            if (unbekannt)
                fahrzeug.fahrzeugRegistrieren(nummernschild,typ,false);

            List<Integer> list = myCon.queryList("Select * from garage order by platzNr", rs -> rs.getInt("platzNr"),null);

            // Ermittlung der kleinsten freien PlatzNummer
            int i = 1; // i entspricht PlatzNr
            for (Integer value : list)
            {
                if (value == i)
                    i++;
                else
                    break;
            }

            List<Parketage> etagen =myCon.queryList("Select * from parketage order by etageNr",rs->new Parketage(rs.getInt("etageNr"),rs.getInt("anzahlPlaetze")),null);
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
                int insert = 0;
                String[][] probsList = {{"" + i, nummernschild.toUpperCase(), "" + etageNr}};
                // Parkplatz wird zugewiesen
                insert = myCon.executeUpdate("INSERT INTO garage (platzNr, Fahrzeug_nummernschild, Parketage_etageNr) VALUES (?,?,?)", probsList);

                if (insert != 0)
                {
                    String[] viewInfo = new String[]{typ, nummernschild, "" + etageNr, "" + i};
                    pch.propertyChange("ZeigePos", viewInfo);
                }
            }


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
        if(fahrzeug.checkNummernschild(nummernschild))
        {
            String[][] probsList = new String[1][1];
            String[] probs = new String[1];
            probs[0]=nummernschild;
            probsList[0]=probs;
            int count = myCon.executeUpdate("DELETE FROM parkhaus.garage WHERE (Fahrzeug_nummernschild = ?)", probsList );

            if(count !=0)
                pch.propertyChange("Verlassen", nummernschild);
            else
                pch.propertyChange("Fail", nummernschild);
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
        if (fahrzeug.checkNummernschild(nummernschild))
        {
            String[] conInfo = new String[] {nummernschild};
            List<String[]> pos = myCon.queryList
                                (
                              "Select Parketage_etageNr, platzNr, typ " +
                                    "from garage join fahrzeug " +
                                     "on Fahrzeug_nummernschild = nummernschild " +
                                     "where Fahrzeug_nummernschild = ?",
                            rs -> new String[]
                                    {
                                        rs.getString(3),
                                        nummernschild,
                                        rs.getString(1),
                                        rs.getString(2),

                                    },conInfo
                                );
            if (!pos.isEmpty())
                pch.propertyChange("ZeigePos", pos.get(0));
            else
                pch.propertyChange("Fail", nummernschild);
        }

    }

    /**
     * Liest alle belegten Parkplätze und schickt sie als Liste an die View, die
     * daraus ihre Tabelle füllt.
     */
    public void parkplatzTabelle()
    {
        List<Garage> liste_Garage = myCon.queryList
                ("Select * from parkhaus.garage;",
                rs -> new Garage(rs.getInt(
                        "platzNr"),
                        rs.getString("Parketage_etageNr"),
                        rs.getString("Fahrzeug_nummernschild")),null
                );
        pch.propertyChange("TabAn", liste_Garage);
        pch.propertyChange("PanelTabelle", null);
    }


}
