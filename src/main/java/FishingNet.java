import java.util.Date;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;

@Entity
@Named
@RequestScoped
public class FishingNet
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int ID;
	private Double longitude;
	private Double latitude;
	private NetStatus status;
	private Date created; 
	
	public enum NetStatus {
		REPORTED,
		IN_PROGRESS,
		SECURED,
		LOST
	}
	
	 public FishingNet()
	 {	
		 
	 }
	 
	 public void setLongitude(Double lng)
	 {
		 this.longitude = lng;
	 }
	 
	 public Double getLongitude()
	 {
		 return this.longitude;
	 }
	 
	 public void setLantitude(Double lat)
	 {
		 this.latitude = lat;
	 }
	 
	 public Double getLantitude()
	 {
		 return this.latitude;
	 }
	 
	 public void setStatus(NetStatus newStatus)
	 {
		 this.status = newStatus;
	 }
	 
	 public NetStatus getStatus()
	 {
		 return this.status;
	 }
	 
	 public void setCreated(Date date)
	 {
		 this.created = date;
	 }
	 
	 public Date getCreated()
	 {
		 return this.created;
	 }
	
}