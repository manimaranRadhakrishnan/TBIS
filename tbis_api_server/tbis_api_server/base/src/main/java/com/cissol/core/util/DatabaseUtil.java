package com.cissol.core.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.cissol.core.servlet.InitProjectServlet;

public class DatabaseUtil {
	private static String FS = System.getProperty("file.separator");
	private static String appPath = System.getProperty("app.path");
	private static String connectionURL = "";
	public static int databaseType = 2;
	public static String driverName = "";
	public static String defaultDatasourceName = "jdbc/mydb";
	private static Driver driver = null;

	private static String getConfigDataProperty(Properties dbConfig, String prefix, String propname) {
		// Example: String value = dbConfig.getProperty(prefix+"Database_Driver");
		String value = dbConfig.getProperty(prefix + propname);
		if (value == null && prefix.length() > 0) {
			value = dbConfig.getProperty(propname);
		}
		return value;
	}

	public static String getConnectionString(Properties dbConfig, String prefix) throws Exception {
		Properties con_prop = new Properties();

		String tnsName = "";
		String baseURL = "";
		String url = "";
		String driverName = "";
		String dbPortNo;
		String userName;
		String passWord;
		String ipAddress;
		String dbName = "";
		String instanceName = "";
		String defauldbName = "tbis";

		driverName = getConfigDataProperty(dbConfig, prefix, "Driver");
		if (driverName != null) {
			driverName = driverName.trim();
		} else {
			throw new Exception(
					"Could not able to get the value for the property 'Driver' in db_config.ini file [prefix=" + prefix
							+ "]");
		}
		dbPortNo = getConfigDataProperty(dbConfig, prefix, "PortNo");
		dbPortNo = dbPortNo.trim();

		dbName = getConfigDataProperty(dbConfig, prefix, "Name");
		if (dbName == null || "".equals(dbName)) {
			dbName = defauldbName;
		}
		if (dbName != null) {
			dbName = dbName.trim();
		}
		userName = getConfigDataProperty(dbConfig, prefix, "UserName");
		if (userName != null) {
			userName = userName.trim();
		}
		passWord = getConfigDataProperty(dbConfig, prefix, "Password");
		if (passWord != null) {
			passWord = passWord.trim();
		}
		ipAddress = getConfigDataProperty(dbConfig, prefix, "IPAddress");
		if (ipAddress != null) {
			ipAddress = ipAddress.trim();
		}
		instanceName = getConfigDataProperty(dbConfig, prefix, "InstanceName");
		if (instanceName == null) {
			instanceName = "";
		} else {
			instanceName = instanceName.trim();
		}
		if ("oracle".equalsIgnoreCase(driverName)) {
			driverName = "oracle.jdbc.driver.OracleDriver";
			tnsName = getConfigDataProperty(dbConfig, prefix, "Tns_Name");
			if (tnsName != null) {
				tnsName = tnsName.trim();
			}
			baseURL = "jdbc:oracle:thin:@" + ipAddress + ":" + dbPortNo + ":" + tnsName;
			url = baseURL;
			databaseType = 1;
			Class.forName(driverName);
		} else if ("mysql".equalsIgnoreCase(driverName)) {
			driverName = "com.mysql.jdbc.Driver";
			baseURL = "jdbc:mysql://" + ipAddress + ":" + dbPortNo + "/" + dbName;
			url = baseURL + "?user=" + userName + "&password=" + passWord;
			databaseType = 0;
			Class.forName(driverName);
		} else if ("postgres".equalsIgnoreCase(driverName)) {
			driverName = "org.postgresql.Driver";
			baseURL = "jdbc:postgresql://" + ipAddress + ":" + dbPortNo + "/" + dbName;
			url = baseURL + "?user=" + userName + "&password=" + passWord;
			databaseType = 3;
			Class.forName(driverName);
		} else if ("mssql".equalsIgnoreCase(driverName)) {
			driverName = "net.sourceforge.jtds.jdbc.Driver";
			String myInstance = null;
			if ("".equals(instanceName)) {
				myInstance = "";
			} else {
				myInstance = ";instance=" + instanceName;
			}
			// Using Port No
			baseURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + dbPortNo + "/" + dbName;
			url = baseURL + myInstance + ";user=" + userName + ";password=" + passWord;
			databaseType = 2;
			Class.forName(driverName);
		}
		return url;
	}

	public static Connection getStandaloneConnection() throws Exception {
		Connection con = null;
		if ("".equals(connectionURL)) {
			Properties dbConfig = new Properties();
			String dbConfigPath = appPath + FS + "dev_help" + FS + "db_config.ini";
			dbConfig.load(new FileInputStream(dbConfigPath));
			connectionURL = getConnectionString(dbConfig, "");
		}
		con = DriverManager.getConnection(connectionURL);
		return con;
	}

	public static Connection getConnection(String dsName) throws Exception {
		Connection conn = null;
		InitialContext initialContext = new InitialContext();
		Context envContext = (Context) initialContext.lookup("java:/comp/env");
		DataSource ds = (DataSource) envContext.lookup(dsName);
		conn = ds.getConnection();
		return conn;
	}


	public static Connection getConnection() throws Exception {
		Connection con = null;
		con = getConnection(defaultDatasourceName);
		return con;
	}

	public static Connection getConnectionFromUrl(String url) throws Exception {
		Connection con = null;
		if (driver == null) {
			Class<?> c = Class.forName("com.mysql.jdbc.Driver");
			driver = (Driver) c.getDeclaredConstructor().newInstance();
		}
		Properties p = new Properties();
		con = driver.connect(url, p);
		return con;
	}
}