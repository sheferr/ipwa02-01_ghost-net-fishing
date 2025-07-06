import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.*;

@Entity
public class FishingNet implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;
	private Double longitude;
	private Double latitude;
	private Double radius;

	private NetStatus status;
	private LocalDate created;

	public enum NetStatus {
		NONE, REPORTED, IN_PROGRESS, SECURED, LOST
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

	public NetStatus getStatus() {
		return this.status;
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
}