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

		DatabaseLogin login = new DatabaseLogin();

		// berättar för spark vart den ska leta efter filer som används.
		staticFileLocation("/public");
		
		// get route för startsidan, d.v.s. "localhost:4567/"
		get("/", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			
			System.out.println(request.headers());
			request.headers();

			// If-sats som skickar information till index.html om användaren är inloggad
			// eller inte.
			String user = request.session().attribute("loginName");
			if (null != user) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}
			
			ArrayList<JSON> games = new ArrayList<JSON>();

			// Skapar objekt av den information som hämtas från databasen för att visa på
			// startsidan
			model.put("games", games);
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
			
//			request.queryParams("");
			
			System.out.println();

			
			String user = request.session().attribute("goob");
			System.out.println(user);
			if (null != user) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}

//			model.put();
			return new ModelAndView(model, "templates/index.html");
		}, new VelocityTemplateEngine());

		// metod för att registrera användare
		get("/register", (request, response) -> {
			Map<String, Object> model = new HashMap<>();

			String username = request.queryParams("txtName");
			String pass1 = request.queryParams("txtPass1");
			String pass2 = request.queryParams("txtPass2");

			if (pass1.equals(pass2)) {
				try {
					Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
							login.getPassword());
					PreparedStatement stmt = con.prepareStatement("select username from users where username=?");
					stmt.setString(1, username);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						if (username.equals(rs.getString(1))) {
							System.out.println("username exists!");
							System.out.println(rs.next());
							model.put("userExists", "true");
							request.session().attribute("userExists", "true");
							response.redirect("index.html");
						}
					} else {
						System.out.println("registering user...");
						PreparedStatement stmt2 = con.prepareStatement("insert into users values (?,?)");
						stmt2.setString(1, username);
						stmt2.setString(2, pass1);
						stmt2.executeUpdate();
						request.session(true);
						request.session().attribute("loginName", username);
						response.redirect("index.html");
					}
					con.close();
					rs.close();
				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				response.redirect("index.html");
			}
			return render(model, "templates/index.html");
		});

		// metod som loggar in användare
		get("/login", (request, response) -> {
			Map<String, Object> model = new HashMap<>();

			String loginName = request.queryParams("loginName");
			String loginPass = request.queryParams("loginPass");

			try {
				Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
						login.getPassword());
				PreparedStatement stmt = con.prepareStatement("select password from users where username=?");
				stmt.setString(1, loginName);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					if (loginPass.equals(rs.getString(1))) {
						request.session(true);
						request.session().attribute("loginName", loginName);
						System.out.println("Lösenord stämmer, loggar in användare");
						request.session().attribute("loginName");
						response.redirect("index.html");
					} else {
						model.put("wrongPassword", "true");
						request.session().attribute("wrongPassword", "true");
						response.redirect("index.html");
					}
				} else {
					System.out.println("Lösenord stämmer inte");
					model.put("wrongPassword", "true");
					request.session().attribute("wrongPassword", "true");
					response.redirect("index.html");
				}
				con.close();
				rs.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			return new ModelAndView(model, "templates/index.html");
		}, new VelocityTemplateEngine());

		// metod som loggar ut användaren
		get("/logout", (request, response) -> {

			String userID = request.session().attribute("loginName");

			if (null != userID) {
				request.session().removeAttribute("loginName");
			}

			response.redirect("index.html");
			return "templates/index.html";
		});



		// sökfunktion
		get("/Search", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			ArrayList<JSON> trips = new ArrayList<JSON>();
			// det användaren skriver in i sökrutan sparas i userSearch
			String userSearch = request.queryParams("searchbar");
			String userSearch2 = request.queryParams("searchbar2");
			System.out.println(userSearch);
			System.out.println(userSearch2);

			String userID = request.session().attribute("loginName");
			if (null != userID) {
				model.put("loggedIn", "true");
			} else {
				model.put("loggedIn", "false");
			}

			try {
				Connection con = DriverManager.getConnection(login.getJdbUrl(), login.getUsername(),
						login.getPassword());
				PreparedStatement stmt = con.prepareStatement("select * from Stretches where LOWER(City1) = ? and LOWER(City2) = ?");
				
				stmt.setString(1, userSearch.toLowerCase());
				stmt.setString(2, userSearch2.toLowerCase());
				int counter = 0;
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String route = rs.getString(1);
					System.out.println("Stad: " + route + " " + rs.getString(2));
					counter++;
				}
				System.out.println(counter);
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
					
//					select * from stretches T1, stretches T2
//					where T1.city1=? and T1.city2=T2.city1 and T2.city2='Berlin' funkar för 3 resor
					
					
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
						System.out.println("här "+route);
						cities = route.split("->");
						
//						JSON trip = new JSON(
//								"testt", 
//								"test", 
//								1, 
//								2
//								);
//
//						trips.add(trip);
					}
					
					System.out.println(Arrays.toString(cities));
					
					for (int i = 0; i < cities.length-1; i++) {
//						if (cities.length % 2 == 0) {
//							
//						}
						
 
						stmt = con.prepareStatement("SELECT city1, city2, price, seats, arrival_time, departure_time, day_of_week"
								+ " from Stretches "
								+ " INNER JOIN trips "
								+ " on trips.stretchid=stretches.stretchid "
								+ " INNER JOIN timetable "
								+ " on timetable.stretchid=stretches.stretchid "
								+ " where city1=? AND city2=?;");
						stmt.setString(1, cities[i]);
						stmt.setString(2, cities[i+1]); 
						
						rs = stmt.executeQuery();
						
						JSON trip = null;
						while (rs.next()) {
							trip = new JSON(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7));
							System.out.println("DENNA KÖRS: " + rs.getString(1) + " " + rs.getString(2) + rs.getInt(3) 
							+ rs.getInt(4) 
							+ " " 
							+ rs.getString(5) 
							+ rs.getString(6)
							+ rs.getString(7));
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