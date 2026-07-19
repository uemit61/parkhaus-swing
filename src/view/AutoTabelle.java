package view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.Controller;

/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class AutoTabelle extends JPanel implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;
	private Controller controller = null;
	private JButton btnZuruck;
	private JTable table;
	private DefaultTableModel dtm = null, tempDtm;

	public void addProperty()
	{
		controller.addPropertyListener("AutoTab", this);
	}
	/**
	 * Create the panel.
	 */
	public AutoTabelle(Controller controller) 
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
		scrollPane.setViewportView(table);
	}

	private void btnZuruckActionPerformed(ActionEvent e) 
	{
		table.setModel(new DefaultTableModel());
		controller.propertyChange("ZuruckAdmin");
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		ResultSet rs = (ResultSet) evt.getNewValue();
		dtm = (DefaultTableModel) table.getModel();
		
		try 
		{
			int column = rs.getMetaData().getColumnCount(); // Anzahl der Spalten bei 1 beginnen
			
			String[] columName = new String[column];
			
			columName[0] = "Nummernschild";
			columName[1] = "Typ";
			
			
			dtm.setColumnIdentifiers(columName);
			
			while(rs.next())
			{
				Object[] rowData = new Object[column];
				for (int i = 0; i < rowData.length; i++) 
				{
					rowData[i] = rs.getObject(i+1);
				}
				dtm.addRow(rowData);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	
}
