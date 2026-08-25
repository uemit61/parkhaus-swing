package view;

import javax.swing.JPanel;
import javax.swing.JTable;

import controller.Controller;
import model.entity.Garage;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class ParkplatzTabelle extends JPanel implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;
	private Controller controller = null;
	private JButton btnZuruck;
	private JTable table;
	private GarageTableModel garageTableModel;

	public void addProperty()
	{
		controller.addPropertyListener("TabAn", this);
	}
	/**
	 * Create the panel.
	 */
	public ParkplatzTabelle(Controller controller) 
	{
		
		this.controller = controller;
		addProperty();
		
		setLayout(null);
		
		btnZuruck = new JButton("Zurück");
		btnZuruck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnZuruckActionPerformed(e);
			}
		});
		btnZuruck.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnZuruck.setBounds(60, 50, 85, 30);
		add(btnZuruck);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(60, 114, 420, 391);
		add(scrollPane);
		
		table = new JTable();
		garageTableModel = new GarageTableModel(new ArrayList<>());
		table.setModel(garageTableModel);
		scrollPane.setViewportView(table);
	}

	private void btnZuruckActionPerformed(ActionEvent e) 
	{
		controller.propertyChange("ZuruckAdmin");
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		List<Garage> garageList = (List<Garage>) evt.getNewValue();
		garageTableModel.updateTable(garageList);
	}
}
