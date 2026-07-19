package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class MyConnection
{
	private String url, username, password, database;
	private Connection connector;
	private Statement statement;
	
	public MyConnection(String url, String username, String password)
	{
		this.url = url;
		this.username = username;
		this.password = password;
		
		try
		{
			connector = DriverManager.getConnection(this.url, this.username, this.password);
			this.statement = this.connector.createStatement();

		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}


	public void execute(String query)
	{
		try
		{
			this.statement.execute("" + query);
		}
		catch (SQLException e)
		{
			System.out.println("Oh NO Wrong Query"+e);
		}
	}

	public ResultSet executeQuery(String query)
	{
		ResultSet rs = null;
		
		try
		{
			rs = this.statement.executeQuery(query);
		}
		catch (SQLException e)
		{
			System.out.println("Wrong query, try again!"+e);
		}
			
		return rs;
	}
}


