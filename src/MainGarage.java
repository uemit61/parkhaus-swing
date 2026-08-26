import java.awt.EventQueue;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.util.Properties;

import model.db.MyConnection;
import model.event.PropertyChangeHandle;
import model.service.*;
import model.dao.*;
import view.ViewParkhaus;
import controller.Controller;




/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class MainGarage
{
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				Properties props = new Properties();
				try (FileInputStream in = new FileInputStream("config.properties"))
				{

					 PropertyChangeSupport support = new PropertyChangeSupport(this);
					 PropertyChangeHandle pch = new PropertyChangeHandle(support);

					 props.load(in);
					 MyConnection myCon = new MyConnection(
															props.getProperty("db.url"),
															props.getProperty("db.user"),
															props.getProperty("db.password")
														  );

					 FahrzeugDao fahrzeugDao = new FahrzeugDao(myCon);
					 GarageDao garageDao = new GarageDao(myCon);
					 ParketageDao parketageDao = new ParketageDao(myCon);

					 FahrzeugService fahrzeugService =new FahrzeugService(fahrzeugDao,garageDao,pch);
					 GarageService garageService = new GarageService(fahrzeugService,pch,parketageDao,garageDao,fahrzeugDao);
					 ParketageService parketageService = new ParketageService(parketageDao,pch);




					 Controller controller = new Controller(fahrzeugService,parketageService,garageService,pch);
					 ViewParkhaus window = new ViewParkhaus(controller);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
