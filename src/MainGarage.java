import java.awt.EventQueue;

import view.ViewParkhaus;
import model.Parkhaus;
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
				try {
					Parkhaus model = new Parkhaus();
					
					Controller controller = new Controller(model);
					
					ViewParkhaus window = new ViewParkhaus(controller);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
