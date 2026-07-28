package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ümit Yildirim <hopes61@icloud.com>
 * @copyright Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class MyConnection
{
    private String url, username, password;

    public MyConnection(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection()
    {
        Connection connector = null;
        try
        {
            connector = DriverManager.getConnection(url,username,password);
        }
        catch (SQLException e)
        {
            System.out.println("Es konnte keine Verbindung hergestellt werden."+e.getMessage());
        }
        return connector;
    }

    public int executeUpdate(String query)
    {
        int retVal =0;
        try (Connection con = getConnection();
             Statement st = con.createStatement())
        {
            retVal = st.executeUpdate(query); // Anzahl betroffener Zeile
        }
        catch (SQLException e)
        {
            // Technische Ursache ausgeben. Fachlich deuten koennen nur die Modelle:
            // ein doppeltes Kennzeichen meldet Fahrzeug als "Vorhanden" an die View.
            System.out.println("Update fehlgeschlagen: " + e.getMessage());
        }
        return retVal;
    }

    public <T> List<T> queryList(String query,RowMapper<T> mapper)
    {
        List<T> retVal = new ArrayList<>();
        try(
            Connection con= getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query)
           )
        {
            while(rs.next())
            {
                retVal.add(mapper.mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        return retVal;
    }

}


