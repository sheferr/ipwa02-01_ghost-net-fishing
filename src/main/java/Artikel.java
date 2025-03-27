import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;

@Entity
@Named
@RequestScoped
public class Artikel
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int ID;
    private String name = "Filzpantoffeln 'Rudolph'";

    public Artikel()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
