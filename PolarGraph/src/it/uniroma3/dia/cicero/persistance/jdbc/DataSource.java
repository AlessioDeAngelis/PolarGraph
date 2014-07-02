package it.uniroma3.dia.cicero.persistance.jdbc;


import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;
import org.postgresql.Driver;

import com.mchange.v2.c3p0.ComboPooledDataSource;



import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/*
 * PER UTILIZZARE QUESTA CLASSE, LA LIBRERIA 
 * c3p0-0.9.1.1.jar 
 * DEVE ESSERE NEL BUILD PATH
 * 
 * */


public class DataSource {

	private static Logger logger = Logger.getLogger("it.uniroma3.persistence.jdbc.SimpleDataSource");
	private ComboPooledDataSource cpds;
	private static DataSource instance;

	private DataSource(){
		InputStream inputStream = null;
		Properties conf = new Properties();
		
		try {
			// il file di properties jdbc.properties nel quale scriviamo i parametri per la connessione
			// e' memorizzato nella directory META-INF che si trova alla radice dei sorgenti 
			// (quindi abbiamo src/META-INF/)
			inputStream = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
			conf.load(inputStream);
		} catch (IOException e) {
			logger.severe("Error loading the Data Source property file: "+ e.getMessage());
		}			
		String uri = conf.getProperty("uri");
		String password = conf.getProperty("password");
		String username = conf.getProperty("username");
		String driver = conf.getProperty("driver");
		
		this.cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(driver);
		} catch (PropertyVetoException e) {
			logger.severe("Error Loading Class Driver " + e.getMessage());
		} 
		this.cpds.setJdbcUrl(uri);
		this.cpds.setUser(username);                                  
		this.cpds.setPassword(password);                                  

		// altre impostazioni (si potrebbero omettere e usare i valori di default)
		cpds.setMinPoolSize(5);                                     
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);		
	}

	public static DataSource getInstance(){
		if (instance==null) 
			instance = new DataSource();
		return instance;
	}

	public Connection getConnection()  {
		Connection connection = null;
		try {
			connection = cpds.getConnection();
		} catch (SQLException e) {
			logger.severe("Connessione non creata "+ e.getMessage());
		}
		return connection;
	}
}
