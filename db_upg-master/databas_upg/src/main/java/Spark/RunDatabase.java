package Spark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Denna klass kör DatabaseController för att initialisera databasen
 * och skickar in den information som hemsidan använder
 */

public class RunDatabase {

	// inlogg till databas

	String jdbUrl = "jdbc:postgresql://localhost:5432/postgres";
	String username = "postgres";
	String password = "password";

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;

	public RunDatabase() {
	}

	public void connect() {
		try {
			conn = DriverManager.getConnection(jdbUrl, username, password);

			System.out.println("Database connection established");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (stmt != null) {
				stmt.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
//		System.out.println("Startar!");
//
//		DatabaseSetup ctrl = new DatabaseSetup();
//		ctrl.connect();
//		ctrl.createTables();
//		ctrl.insertValues();
//		ctrl.disconnect();
		

		    LocalDate date = LocalDate.now();
		    WeekFields weekFields = WeekFields.of(Locale.getDefault());
		    System.out.println(date.get(weekFields.weekOfWeekBasedYear()));
		
		
	}
		
	}
		

