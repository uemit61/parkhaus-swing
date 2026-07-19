package controller;

import java.beans.PropertyChangeListener;

import model.Parkhaus;


/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class Controller 
{
	private Parkhaus model = null;
	
	public Controller(Parkhaus model)
	{
		this.model = model;
	}
	

	
	public void addPropertyListener(String propName, PropertyChangeListener view)
	{
		model.addPropertyChangeListener(propName, view);
	}
	
	public void propertyChange(String propName)
	{
		model.propertyChange(propName, null);
	}
	
	public void freiePlaetze()
	{
		model.freiePlaetze();
	}
	
	public void befahren(String nummernschild, String typ)
	{
		model.befahren(nummernschild,typ);
	}
	
	public void verlassen(String nummernschild)
	{
		model.verlassen(nummernschild);
	}
	
	public void zeigePos(String nummernschild)
	{
		model.zeigePostion(nummernschild);
	}
	
	public void fahrzeugRegistrieren(String nummernschild,String typ)
	{
		model.fahrzeugRegistrieren(nummernschild,typ);
	}
	
	public void loeschen(String nummernschild, String typ)
	{
		model.loescheFahrzeug(nummernschild, typ);
	}
	
	public void parkplatzTabelle()
	{
		model.parkplatzTabelle();
	}
	
	public void autoTabelle()
	{
		model.autoTabelle();
	}
	
}
