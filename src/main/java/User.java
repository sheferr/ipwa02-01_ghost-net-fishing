import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;
	private String name;
	private String mobile;
	
	public User()
	{
		this.mobile = "";
	}
	
	public User(String number)
	{
		this.mobile = number;
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
		this.mobile = number;
	}
	
	public String getMobile()
	{
		return this.mobile;
	}
}