package controller;

import java.beans.PropertyChangeListener;

import model.event.PropertyChangeHandle;
import model.service.*;


/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Controller 
{
	private FahrzeugService modelFahrzeug = null;
	private ParketageService modelParketage = null;
	private GarageService modelGarage = null;
	private PropertyChangeHandle pch;

	//Constructor


	public Controller(FahrzeugService modelFahrzeug, ParketageService modelParketage, GarageService modelGarage,PropertyChangeHandle pch)
	{
		this.modelFahrzeug = modelFahrzeug;
		this.modelParketage = modelParketage;
		this.modelGarage = modelGarage;
		this.pch = pch;
	}

	public void addPropertyListener(String propName, PropertyChangeListener view)
	{
		pch.addPropertyChangeListener(propName, view);
	}
	
	public void propertyChange(String propName)
	{
		pch.propertyChange(propName, null);
	}
	
	public void freiePlaetze()
	{
		modelParketage.freiePlaetze();
	}
	
	public void befahren(String nummernschild, String typ)
	{
		modelGarage.befahren(nummernschild,typ);
		modelParketage.freiePlaetze();
	}
	
	public void verlassen(String nummernschild)
	{
		modelGarage.verlassen(nummernschild);
		freiePlaetze();
	}
	
	public void zeigePos(String nummernschild)
	{
		modelGarage.zeigePostion(nummernschild);
	}
	
	public void fahrzeugRegistrieren(String nummernschild,String typ)
	{
		//Propertier 'admin' sagt der Methode, dass Sie vom AdminView aufgerufen wurde
		modelFahrzeug.fahrzeugRegistrieren(nummernschild,typ,true);
	}
	
	public void loeschen(String nummernschild, String typ)
	{
		modelFahrzeug.loescheFahrzeug(nummernschild, typ);
	}
	
	public void parkplatzTabelle()
	{
		modelGarage.parkplatzTabelle();
	}
	
	public void autoTabelle()
	{
		modelFahrzeug.autoTabelle();
	}
	
}
