package Spark;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klass som initialiserar databasen med tabeller och information som används av hemsidan
 *
 */

public class DatabaseSetup extends RunDatabase {
	
	public void createTables() {
		try {
			stmt = conn.createStatement();
			
			String TravelerTable = "CREATE TABLE Travelers ("
					+ "Username varchar(50) PRIMARY KEY,"
					+ "Password varchar(50) NOT NULL,"
					+ "First_name varchar(50),"
					+ "Last_name varchar(50),"
					+ "Phone_number varchar(50),"
					+ "Address varchar(50),"
					+ "Email varchar(50)"
					+ ")";
			
			String DriverTable = "CREATE TABLE Drivers("
					+ "Driver_ID SERIAL PRIMARY KEY,"
					+ "SSN varchar(50) NOT NULL,"
					+ "Name varchar(50),"
					+ "Address varchar (50),"
					+ "Phone_number varchar(50),"
					+ "City varchar(50),"
					+ "Country varchar(50))";
			
			String City = "CREATE TABLE Cities ("
					+ "City varchar(50) PRIMARY KEY, "
					+ "Country varchar(50),"
					+ "Street_name varchar(50))";
			
			String Stretches = "CREATE TABLE IF NOT EXISTS Stretches (" +
					"City1 VARCHAR(50), " +
					"City2 VARCHAR(50), " +
					"StretchID SERIAL, " +
					"Estimated_time INT, " +
					"Price INT, " +
					"PRIMARY KEY(StretchID), " + 
					"FOREIGN KEY(City1) REFERENCES Cities(City), " +
					"FOREIGN KEY(City2) REFERENCES Cities(City)" +
					 ")";
			
			String Trips = "CREATE TABLE IF NOT EXISTS Trips (" +
					"TripID SERIAL, " +
					"StretchID INT, " +
					"DriverID INT, " +
					"Seats INT DEFAULT 30, " +
					"ScheduleID INT, " +
					"FOREIGN KEY(StretchID) REFERENCES Stretches(StretchID), " +
					"FOREIGN KEY(DriverID) REFERENCES Drivers(Driver_ID), " +
					"FOREIGN KEY(ScheduleID) REFERENCES Timetable (ScheduleID), " +
					"PRIMARY KEY (TripID)" +
					 ")";
			
			String Timetable = "CREATE TABLE IF NOT EXISTS Timetable (" +
					"ScheduleID SERIAL, " +
					"StretchID INT, " +
					"Day_of_Week VARCHAR(10), " +
					"Departure_time TIME, " +
					"Arrival_time TIME, " + //arrival date?
					"FOREIGN KEY(StretchID) REFERENCES Stretches(StretchID), " +
					"PRIMARY KEY (ScheduleID)" +
					")";
			
			String Bookings = "CREATE TABLE IF NOT EXISTS Bookings (" +
					"BookingID SERIAL, " +
					"TripID INT, " +
					"DriverID INT, " +
					"Username VARCHAR(50), " +
					"Seat_number INT, " + //between 1-30 check
					"PRIMARY KEY (BookingID), " +
					"FOREIGN KEY(Username) REFERENCES Travelers(Username), " +
					"FOREIGN KEY(TripID) REFERENCES Trips(TripID)" +
					 ")";

			stmt.executeUpdate(TravelerTable);
			stmt.executeUpdate(DriverTable);
			stmt.executeUpdate(City);
			stmt.executeUpdate(Stretches);
			stmt.executeUpdate(Timetable);
			stmt.executeUpdate(Trips);
			stmt.executeUpdate(Bookings);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertValues() {
		
		try {
			stmt = conn.createStatement();
			
		String TravelerValues = "INSERT INTO travelers "
				+ "VALUES('SofieLju', '12345', 'Sofie', 'Ljungcrantz', '0706149975', 'Trastvägen 8', 'sofieljungcrantz@hotmail.com')";
	
		String DriverValues = "INSERT INTO drivers (ssn, name, address, phone_number, city, country)"
				+ "VALUES('196503015455','Rolf Axelsson','Måsvägen 2b', '07012345678', 'Lund', 'Sweden'),"
				+ "('196729921233', 'Farid Naisan','Trastvägen 2b', '0769213132', 'Malmö', 'Sweden'),"
				+ "('196503015455', 'Johan Fredriksson','Gångvägen 22', '070224252556', 'Malmö', 'Sweden'),"
				+ "('196503015455','Fredrik Tango','Tangovägen 12', '070224252556', 'Malmö', 'Sweden')";

		
		String CityValues = "INSERT INTO cities "
				+"VALUES('Malmö', 'Sverige', 'Camillavägen 22'),"
				+"('Lund', 'Sverige', 'Trastvägen 8'),"
				+"('Göteborg', 'Sverige', 'Grönlandsvägen 1'),"
				+"('Köpenhamn', 'Danmark', 'Norre Havnstorv 25'), "
				+"('Oslo', 'Norge', 'Norrlandsvägen 22'),"
				+"('Berlin', 'Tyskland', 'Tysklandsvägen 5'), "
				+"('Stockholm', 'Sverige', 'Centralplan 15'),"
				+"('Luleå', 'Sverige', 'Rådstugatan 11'),"
				+"('Jönköping', 'Sverige', 'Järnvägsgatan 12'),"
				+"('Mörtfors', 'Sverige', 'Kappemålavägen 5'),"
				+"('Ystad', 'Sverige', 'Hamntorget 1')";
		
		String StretchesValues = "INSERT INTO stretches (city1, city2, estimated_time, price)"
				+ "VALUES('Luleå','Stockholm', 3, 400),"
				+ "('Stockholm', 'Luleå', 3, 400),"
				+ "('Oslo', 'Stockholm', 4, 600),"
				+ "('Stockholm', 'Oslo', 4, 600),"
				+ "('Oslo', 'Göteborg', 3, 400),"
				+ "('Göteborg', 'Oslo', 3, 400),"
				+ "('Stockholm', 'Göteborg', 3, 400),"
				+ "('Göteborg', 'Stockholm', 3, 400),"
				+ "('Göteborg', 'Jönköping', 2, 300),"
				+ "('Jönköping', 'Göteborg', 2, 300),"
				+ "('Göteborg', 'Malmö', 3, 400),"
				+ "('Malmö', 'Göteborg', 3, 400),"
				+ "('Malmö', 'Lund', 1, 100),"
				+ "('Lund', 'Malmö', 1, 100),"
				+ "('Malmö', 'Köpenhamn', 1, 100),"
				+ "('Köpenhamn', 'Malmö', 1, 100),"
				+ "('Köpenhamn', 'Berlin', 2, 400),"
				+ "('Berlin', 'Köpenhamn', 2, 400),"
				+ "('Mörtfors', 'Göteborg', 2, 300),"
				+ "('Göteborg', 'Mörtfors', 2, 300),"
				+ "('Ystad', 'Malmö', 1, 100),"
				+ "('Malmö', 'Ystad', 1, 100)";
		
				
		String TimetableValues = "INSERT INTO timetable (stretchid, day_of_week, departure_time, arrival_time)"
				+ "VALUES(1, 'Måndag', '09:00', '12:00'),"
				+ "(2, 'Måndag', '13:00', '16:00'),"
				+ "(3, 'Måndag', '07:00', '11:00'),"
				+ "(4, 'Måndag', '12:00', '16:00'),"
				+ "(5, 'Tisdag', '10:00', '13:00'),"
				+ "(6, 'Tisdag', '14:00', '17:00'),"
				+ "(7, 'Tisdag', '06:00', '09:00'),"
				+ "(8, 'Tisdag', '10:00', '13:00'),"
				+ "(9, 'Onsdag', '11:00', '13:00'),"
				+ "(10, 'Onsdag', '14:00', '16:00'),"
				+ "(11, 'Onsdag', '08:00', '11:00'),"
				+ "(12, 'Onsdag', '12:00', '15:00'),"
				+ "(13, 'Onsdag', '16:00', '17:00'),"
				+ "(14, 'Torsdag', '18:00', '19:00'),"
				+ "(15, 'Torsdag', '11:00', '12:00'),"
				+ "(16, 'Torsdag', '13:00', '14:00'),"
				+ "(17, 'Torsdag', '10:00', '12:00'),"
				+ "(18, 'Fredag', '12:00', '14:00'),"
				+ "(19, 'Fredag', '17:00', '19:00'),"
				+ "(20, 'Fredag', '20:00', '22:00'),"
				+ "(21, 'Fredag', '16:00', '17:00'),"
				+ "(22, 'Fredag', '18:00', '19:00')";
		
		String TripsValues = "INSERT INTO trips (stretchid, driverid, scheduleid)"
				+ "VALUES(1, 1, 1),"
				+ "(2, 1, 2),"
				+ "(3, 2, 3),"
				+ "(4, 2, 4),"
				+ "(5, 3, 5),"
				+ "(6, 3, 6),"
				+ "(7, 4, 7),"
				+ "(8, 4, 8),"
				+ "(9, 1, 9),"
				+ "(10, 1, 10),"
				+ "(11, 2, 11),"
				+ "(12, 2, 12),"
				+ "(13, 3, 13),"
				+ "(14, 3, 14),"
				+ "(15, 4, 15),"
				+ "(16, 4, 16),"
				+ "(17, 1, 17),"
				+ "(18, 1, 18),"
				+ "(19, 2, 19),"
				+ "(20, 2, 20),"
				+ "(21, 3, 21),"
				+ "(22, 3, 22)";
		
		String BookingsValues = "INSERT INTO bookings ("
				+ ""
				+ ""
				+ ""
				+ ""
				+ ""
				+ ""
				+ ""
				+ ""
				+ ")";

		
		stmt.executeUpdate(TravelerValues);
		stmt.executeUpdate(DriverValues);
		stmt.executeUpdate(CityValues);
		stmt.executeUpdate(StretchesValues);
		stmt.executeUpdate(TimetableValues);
		stmt.executeUpdate(TripsValues);
//		stmt.executeUpdate(Bookings);
		stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
