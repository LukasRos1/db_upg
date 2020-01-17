package Spark;

import static spark.Spark.get;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

/**
 * Controller hanterar hemsidans olika URI och URL.
 */
public final class Controller {

	public static String render(Map<String, Object> model, String templatePath) {
		return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
	}

	public static void main(final String[] args) {

		//if rs.getWeek = java.this.week - 
		DatabaseLogin login = new DatabaseLogin();

		staticFileLocation("/public");
		
		get("/bookings", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			
			String user = request.session().attribute("loginName");
			
			if (null != user) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}
			
			try {
				Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
						login.getPassword());
				PreparedStatement stmt = con.prepareStatement("SELECT stretches.city1, stretches.city2, stretches.price, bookings.seat_number, "
						+ "timetable.arrival_time, timetable.departure_time, timetable.day_of_week, stretches.stretchid, trips.tripid, week"
						+ " from bookings "
						+ " INNER JOIN trips "
						+ " on trips.tripid=bookings.tripid "
						+ " inner join stretches "
						+ " on trips.stretchid=stretches.stretchid "
						+ " INNER JOIN timetable "
						+ " on timetable.stretchid=trips.stretchid "
						+ " where username=?");

				stmt.setString(1, user);
				
				ResultSet rs = stmt.executeQuery();
				ArrayList<JSON> bookings = new ArrayList<JSON>();
				while (rs.next()) {
					JSON booking = new JSON(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getInt(9), rs.getInt(10));
					bookings.add(booking);
				} 
				model.put("bookings", bookings);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return new ModelAndView(model, "templates/bookings.html");
		}, new VelocityTemplateEngine());
		
		// get route för startsidan, d.v.s. "localhost:4567/"
		get("/", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			String user = request.session().attribute("loginName");
			if (null != user) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}
			return new ModelAndView(model, "templates/index.html");
		}, new VelocityTemplateEngine());

		get("/login.html", (request, response) -> {
			Map<String, Object> model = new HashMap<>();

			String userID = request.session().attribute("loginName");
			if (null != userID) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}

			// två if-satser nedan skickar med information som behövs i login.html för att
			// skicka ut felmeddelanden
			String userExists = request.session().attribute("userExists");
			if (userExists == "true") {
				model.put("userExists", "true");
			}

			String wrongPassword = request.session().attribute("wrongPassword");
			if (wrongPassword == "true") {
				model.put("wrongPassword", "true");
			}

			request.session().attribute("wrongPassword", "false");
			request.session().attribute("userExists", "false");
			return new ModelAndView(model, "templates/index.html");
		}, new VelocityTemplateEngine());

		get("/trips", (request, response) -> {
			Map<String, Object> model = new HashMap<>();

			String userID = request.session().attribute("loginName");
			if (null != userID) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}



			return new ModelAndView(model, "templates/trips.html");
		}, new VelocityTemplateEngine());

		get("/buy", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			
			int id = Integer.parseInt(request.queryParams("buy"));
			int seatsLeft = 0;
			String userID = request.session().attribute("loginName");
			
			if (userID!=null) {
					try {
						Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
								login.getPassword());
						PreparedStatement stmt = con.prepareStatement("Select seats from Trips where tripid=?");
						stmt.setInt(1, id);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()) {
							System.out.println(rs.getInt(1));
							seatsLeft = rs.getInt(1);
							if (seatsLeft>=1) {
							stmt = con.prepareStatement("UPDATE trips SET seats=seats-1 where tripid=?");
							stmt.setInt(1, id);
							stmt.executeUpdate();
							System.out.println(5);
							PreparedStatement stmt2 = con.prepareStatement("insert into bookings (tripid, username, seat_number) VALUES(?,?,?)");
							stmt2.setInt(1, id);
							stmt2.setString(2, userID);
							stmt2.setInt(3, seatsLeft);
							stmt2.executeUpdate();
							seatsLeft--;
							System.out.println("klar");
							}
						}
						
//						stmt = con.prepareStatement("Select seats from Trips where stretchid=?");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			String user = request.session().attribute("loggedIn");
			if (null != user) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}

//			model.put();
			return new ModelAndView(model, "templates/trips.html");
		}, new VelocityTemplateEngine());

		// metod för att registrera användare
		get("/register", (request, response) -> {
			Map<String, Object> model = new HashMap<>();

			String username = request.queryParams("Username");
			String password1 = request.queryParams("Password1");
			String password2 = request.queryParams("Password2");
			String firstName = request.queryParams("firstName");
			String lastName = request.queryParams("lastName");
			String Adress = request.queryParams("Adress");
			String Email = request.queryParams("Email");
			String Phonenumber = request.queryParams("Phonenumber");

			if (password1.equals(password2)) {
				try {
					Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
							login.getPassword());
					PreparedStatement stmt = con.prepareStatement("select username from travelers where username=?");
					stmt.setString(1, username);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						if (username.equals(rs.getString(1))) {
							System.out.println("Användare finns redan!");
							response.redirect("/");
						}
					} else {
						PreparedStatement TravelerStmt = con.prepareStatement("insert into travelers values (?,?,?,?,?,?,?)");
						TravelerStmt.setString(1, username);
						TravelerStmt.setString(2, password1);
						TravelerStmt.setString(3, firstName);
						TravelerStmt.setString(4, lastName);
						TravelerStmt.setString(5, Phonenumber);
						TravelerStmt.setString(6, Adress);
						TravelerStmt.setString(7, Email);
						TravelerStmt.executeUpdate();
						System.out.println("Registrering lyckad");
						request.session(true);
						request.session().attribute("loginName", username);
						response.redirect("trips");
					}
					con.close();
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Lösenord stämmer inte överrens...");
				response.redirect("/");
			}
			return render(model, "templates/index.html");
		});

		// metod som loggar in användare
		get("/login", (request, response) -> {
			Map<String, Object> model = new HashMap<>();

			String LoginUsername = request.queryParams("LoginUsername");
			String LoginPassword = request.queryParams("LoginPassword");
			System.out.println(LoginUsername + " " + LoginPassword);

			try {
				Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
						login.getPassword());
				PreparedStatement stmt = con.prepareStatement("select password from travelers where username=?");
				
				stmt.setString(1, LoginUsername);
				ResultSet rs = stmt.executeQuery();
				
				if (rs.next()) {
					if (LoginPassword.equals(rs.getString(1))) {
						request.session(true);
						request.session().attribute("loginName", LoginUsername);
						System.out.println("Lösenord stämmer");
						
						String user = request.session().attribute("loginName");
						
						System.out.println("session attribute: " + user);
						
						response.redirect("trips");
					} else {
						response.redirect("index.html");
					}
				} else {
					System.out.println("Lösenord stämmer inte");
					response.redirect("/");
				}
				con.close();
				rs.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			return new ModelAndView(model, "templates/trips.html");
		}, new VelocityTemplateEngine());

		// metod som loggar ut användaren
		get("/logout", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			String userID = request.session().attribute("loginName");

			if (null != userID) {
				request.session().removeAttribute("loginName");
			}

			return new ModelAndView(model, "templates/index.html");
		}, new VelocityTemplateEngine());



		// sökfunktion
		get("/Search", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			ArrayList<JSON> trips = new ArrayList<JSON>();
			// det användaren skriver in i sökrutan sparas i userSearch
			String userSearch = request.queryParams("searchbar");
			String userSearch2 = request.queryParams("searchbar2");
			userSearch = userSearch.substring(0, 1).toUpperCase() + userSearch.substring(1);
			userSearch2 = userSearch2.substring(0, 1).toUpperCase() + userSearch2.substring(1);
			System.out.println(userSearch);

			String userID = request.session().attribute("loginName");
			if (null != userID) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}

			try {
				Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
						login.getPassword());
				PreparedStatement stmt = con.prepareStatement("SELECT city1, city2, price, seats, arrival_time, departure_time, day_of_week, stretches.stretchid, tripid, week"
						+ " from Stretches "
						+ " INNER JOIN trips "
						+ " on trips.stretchid=stretches.stretchid "
						+ " INNER JOIN timetable "
						+ " on timetable.stretchid=stretches.stretchid "
						+ " where city1=? AND city2=?;");
				stmt.setString(1, userSearch);
				stmt.setString(2, userSearch2); 
				int counter = 0;
				
				ResultSet rs = stmt.executeQuery();

				JSON trip = null;
				while (rs.next()) {
					trip = new JSON(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getInt(9), rs.getInt(10));
					trips.add(trip);
					System.out.println("nu e vi här");
					counter++;
				}
				
				if (counter >= 1) {
					model.put("trips", trips);
				}
			if (counter < 1) {
				System.out.println("counter: "+counter);
				try {
					con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
							login.getPassword());
					stmt = con.prepareStatement("select concat(City1,'->',City2) as stretch from Stretches where LOWER(City1) = ? and LOWER(City2) = ?\r\n" + 
							"union \r\n" + 
							"select concat(r1.City1,'->',r1.City2,'->',r2.City2) as stretch from stretches as r1 \r\n" + 
							"join stretches as r2 on r1.City2 = r2.City1\r\n" + 
							"where LOWER(r1.City1) = ? and LOWER(r2.City2) = ?\r\n" + 
							"union \r\n" + 
							"select concat(r1.City1,'->',r1.City2,'->',r2.City2,'->',r3.City2) as stretch from stretches as r1 \r\n" + 
							"join stretches as r2 on r1.City2 = r2.City1\r\n" + 
							"join stretches as r3 on r2.City2 = r3.City1\r\n" + 
							"where LOWER(r1.City1) = ? and LOWER(r3.City2) = ?;\r\n");
					
					
					
					stmt.setString(1, userSearch.toLowerCase());
					stmt.setString(2, userSearch2.toLowerCase()); 
					stmt.setString(3, userSearch.toLowerCase());
					stmt.setString(4, userSearch2.toLowerCase()); 
					stmt.setString(5, userSearch.toLowerCase());
					stmt.setString(6, userSearch2.toLowerCase()); 

					rs = stmt.executeQuery();
					String[] cities = null;
					while (rs.next()) {
						String route = rs.getString(1);
						cities = route.split("->");
					}
					
					
					for (int i = 0; i < cities.length-1; i++) {
						stmt = con.prepareStatement("SELECT city1, city2, price, seats, arrival_time, departure_time, day_of_week, stretches.stretchid, tripid, week"
								+ " from Stretches "
								+ " INNER JOIN trips "
								+ " on trips.stretchid=stretches.stretchid "
								+ " INNER JOIN timetable "
								+ " on timetable.stretchid=stretches.stretchid "
								+ " where city1=? AND city2=?;");
						stmt.setString(1, cities[i]);
						stmt.setString(2, cities[i+1]); 
						
						rs = stmt.executeQuery();
						
						trip = null;
						while (rs.next()) {
							trip = new JSON(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getInt(9), rs.getInt(10));
							trips.add(trip);
						}
						
					}
					model.put("trips", trips);
					con.close();
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} }catch (Exception e) {
				e.printStackTrace();
			}

			return render(model, "templates/trips.html");
		});
	}
}