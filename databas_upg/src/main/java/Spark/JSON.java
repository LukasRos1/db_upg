package Spark;

/**
 * @author Lukas Rosberg
 * Klass som skapar objekt med information som behövs till html-sidor
 * Flera olika konstruktorer 
 */

public class JSON {

	private int estimated_time;
	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	private String city2;
	private int price;
	private String city1;
	private int seats;
	private String arrival;
	private String departure;
	private String day_of_week;
	
	public JSON (String city1, String city2, int price, int seats, String arrival, String departure, String day_of_week) {
		this.city1=city1;
		this.city2=city2;
		this.price=price;
		this.seats=seats;
		this.arrival=arrival;
		this.departure=departure;
		this.day_of_week=day_of_week;
	}
	
	public int getEstimated_time() {
		return estimated_time;
	}

	public void setEstimated_time(int estimated_time) {
		this.estimated_time = estimated_time;
	}

	public String getCity2() {
		return city2;
	}

	public void setCity2(String city2) {
		this.city2 = city2;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getCity1() {
		return city1;
	}

	public void setCity1(String city1) {
		this.city1 = city1;
	}

	public JSON(String string, String string2, int int1, int int2) {
		this.city1=string;
		this.city2=string2;
		this.estimated_time=int1;
		this.price=int2;
	}


    
}