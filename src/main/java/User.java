import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Named
@RequestScoped
public class User
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int ID;
	private String name;
	private String mobile_number;
	
	public User()
	{		
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setMobile(String number)
	{
		this.mobile_number = number;
	}
	
	public String getMobile()
	{
		return this.mobile_number;
	}
	
}