package view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

import controller.Controller;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;
import java.awt.Color;


/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class AdministrationsGui extends JPanel implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;
	private JButton btnLöschen;
	private JButton btnEinfuegen;
	private JButton btnZurueck;
	private Controller controller =null;
	private JComboBox boxTyp;
	private JTextField numSchEingabe;
	private JLabel lblInfo;
	
	public void addProperty()
	{
		controller.addPropertyListener("FailCheck", this);
		controller.addPropertyListener("Regist", this);
		controller.addPropertyListener("Loeschen", this);
		controller.addPropertyListener("LoeschenFail", this);
	}

	/**
	 * Create the panel.
	 */
	public AdministrationsGui(Controller controller) 
	{
		
		this.controller = controller;
		addProperty();
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Adminbereich");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(252, 30, 136, 45);
		add(lblNewLabel);
		
		JLabel lblNumSch = new JLabel("Nummernschild:");
		lblNumSch.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNumSch.setBounds(60, 172, 120, 20);
		add(lblNumSch);
		
		numSchEingabe = new JTextField();
		numSchEingabe.setBounds(190, 170, 100, 30);
		add(numSchEingabe);
		numSchEingabe.setColumns(10);
		
		btnLöschen = new JButton("Löschen");
		btnLöschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				 btnLöschenActionPerformed(e);
			}
		});
		btnLöschen.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnLöschen.setBounds(490, 168, 100, 30);
		add(btnLöschen);
		
		btnEinfuegen = new JButton("Einfügen");
		btnEinfuegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnEinfuegenActionPerformed(e);
			}
		});
		btnEinfuegen.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEinfuegen.setBounds(337, 168, 100, 30);
		add(btnEinfuegen);
		
		btnZurueck = new JButton("Zurück");
		btnZurueck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnZuruekActionPerformed(e);
			}
		});
		btnZurueck.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnZurueck.setBounds(60, 299, 100, 30);
		add(btnZurueck);
		
		JButton btnAutoTabelle = new JButton("Autotabelle");
		btnAutoTabelle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnAutoTabelleActionPerformed(e);
			}
		});
		btnAutoTabelle.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAutoTabelle.setBounds(475, 299, 115, 30);
		add(btnAutoTabelle);
		
		lblInfo = new JLabel("");
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblInfo.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		lblInfo.setBounds(61, 230, 529, 30);
		add(lblInfo);
		
		JList list = new JList();
		list.setBounds(322, 216, 1, 1);
		add(list);
		
		boxTyp = new JComboBox();
		boxTyp.setModel(new DefaultComboBoxModel(new String[] {"Auto", "Motorrad"}));
		boxTyp.setFont(new Font("Tahoma", Font.BOLD, 14));
		boxTyp.setBounds(190, 120, 100, 30);
		add(boxTyp);
		
		JLabel lblNewLabel_1 = new JLabel("Fahrzeugtyp:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(60, 125, 100, 20);
		add(lblNewLabel_1);
		
		JButton btnParplatzliste = new JButton("Parkplatztabelle");
		btnParplatzliste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnParkplaetzeActionPErformed(e);
			}
		});
		btnParplatzliste.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnParplatzliste.setBounds(247, 299, 136, 30);
		add(btnParplatzliste);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 85, 639, 2);
		add(separator);

	}

	private void btnAutoTabelleActionPerformed(ActionEvent e) 
	{
		controller.autoTabelle();
	}

	private void btnParkplaetzeActionPErformed(ActionEvent e) 
	{
		controller.parkplatzTabelle();
	}

	private void btnEinfuegenActionPerformed(ActionEvent e) 
	{
		lblInfo.setText("");
		controller.fahrzeugRegistrieren(numSchEingabe.getText(),(String) boxTyp.getSelectedItem());
		numSchEingabe.setText("");
		boxTyp.setSelectedIndex(0);
	}

	private void btnZuruekActionPerformed(ActionEvent e) 
	{
		controller.propertyChange("Zuruck");;
	}

	private void btnLöschenActionPerformed(ActionEvent e) 
	{
		controller.loeschen(numSchEingabe.getText(), (String) boxTyp.getSelectedItem());
		numSchEingabe.setText("");
		boxTyp.setSelectedIndex(0);
	}

	
	public JLabel getLblInfo() {
		return lblInfo;
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		if(evt.getPropertyName().equals("FailCheck"))
		{
			String numSchild = (String) evt.getNewValue();
			
			lblInfo.setText("Das Nummernschild '"+numSchild+"' hat ein falsches Format.");
		}
		else if(evt.getPropertyName().equals("Regist"))
		{
			String[] pos = (String[]) evt.getNewValue();
			
			lblInfo.setText("Das "+pos[1]+" '"+pos[0]+"' wurde registriert");
		}
		else if(evt.getPropertyName().equals("Loeschen"))
		{
			String[] pos = (String[]) evt.getNewValue();
			lblInfo.setText("Das "+pos[1]+" '"+pos[0]+"' wurde gelöscht");
		}
		else if(evt.getPropertyName().equals("LoeschenFail"))
		{
			String[] pos = (String[]) evt.getNewValue();
			lblInfo.setText("Das "+pos[1]+" '"+pos[0]+"' ist nicht registriert");
		}
	}
}
