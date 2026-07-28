import java.awt.EventQueue;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.util.Properties;

import model.*;
import view.ViewParkhaus;
import controller.Controller;




/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class MainGarage {
	
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


					 Fahrzeug modelFahrzeug = new Fahrzeug();
					 modelFahrzeug.inits(myCon,pch);

					 Garage modelGarage = new Garage();
					 modelGarage.inits(myCon,pch,modelFahrzeug);


					 Parketage modelParketage = new Parketage();
					 modelParketage.inits(myCon,pch);

					 Controller controller = new Controller(modelFahrzeug,modelGarage,modelParketage,pch);
					 ViewParkhaus window = new ViewParkhaus(controller);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
