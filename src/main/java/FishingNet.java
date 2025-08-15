import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class FishingNet implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;
	private Double longitude;
	private Double latitude;
	private Double radius;

	private NetStatus status;
	private LocalDate created;
	
	@ManyToOne(fetch= FetchType.EAGER)
	private User user;

	public enum NetStatus {
		NONE      (Color.LIGHT_GRAY,   Color.DARK_GRAY),
	    REPORTED  (Color.ORANGE,       Color.RED),
	    IN_PROGRESS(Color.YELLOW,      Color.ORANGE),
	    SECURED   (Color.GREEN,        Color.DARK_GRAY),
	    LOST      (Color.RED,          Color.BLACK);
		
		
		private final Color fillColor;
		private final Color strokeColor;
		private static final NetStatus[] vals = values();
		
		NetStatus(Color fillColor, Color storeColor) {
	        this.fillColor   = fillColor;
	        this.strokeColor = storeColor;
	    }

	    public String getFillColor() {
	        return String.format("#%02X%02X%02X",
	        		fillColor.getRed(),
	        		fillColor.getGreen(),
	        		fillColor.getBlue());
	    }

	    public String getStrokeColor() {
	        return String.format("#%02X%02X%02X",
	        		strokeColor.getRed(),
	        		strokeColor.getGreen(),
	        		strokeColor.getBlue());
	    }
	    
	    public NetStatus next() {
	        return vals[(this.ordinal() + 1) % vals.length];
	    }
	}

	public FishingNet() {
		this.longitude = 0.0;
		this.latitude = 0.0;
		this.radius = 0.0;
		this.status = NetStatus.NONE;
	}

	public void setLongitude(Double lng) {
		this.longitude = lng;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLatitude(Double lat) {
		this.latitude = lat;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setStatus(NetStatus newStatus) {
		this.status = newStatus;
	}
	
	public NetStatus getEStatus()
	{
		return this.status;
	}

	public String getStatus() {
		switch(this.status)
		{
		case REPORTED:
			return "Erfasst";
		case IN_PROGRESS:
			return "In Bearbeitung";
		case SECURED:
			return "Geborgen";
		case LOST:
			return "Verschollen";
		case NONE:
			default:
			return "Unbekannt";	
		}
	}
	
	public String getNextStatus()
	{
		switch(this.status.next())
		{
		case REPORTED:
			return "Erfasst";
		case IN_PROGRESS:
			return "Bergung beanspruchen";
		case SECURED:
			return "Geborgen melden";
		case LOST:
			return "Verschollen melden";
		case NONE:
			default:
			return "Unbekannt";
		}
	}

	public void setCreated(LocalDate date) {
		this.created = date;
	}

	public LocalDate getCreated() {
		return this.created;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}
	
	public void setArea(Double area)
	{
		this.radius = Math.sqrt((area * 1_000_000)/Math.PI);
	}
	
	public Double getArea() {
		return (Math.PI * this.radius * this.radius) / 1_000_000;
	}
	
	public int getId()
	{
		return ID;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User newUser)
	{
		System.out.println("Set user: " + newUser);
		this.user = newUser;		
	}
}