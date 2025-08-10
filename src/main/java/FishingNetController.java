import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityTransaction;

@Named
@ApplicationScoped
public class FishingNetController implements Serializable {

	@Inject
	NetDataBase fishingNetDatabase;

	CirclesView circlesView = new CirclesView();
	private FishingNet fishingNet = new FishingNet();
	private List<FishingNet> fishingNetList = new ArrayList<>();
	User currentUser = new User();
	private FishingNet selectedNet;
	
	private boolean showMissing = false;
	private boolean showNext = false;
	private boolean showName = false;
	
	private static final List<String> CONTINENTS = List.of(
            "Nordamerika",
            "Suedamerika",
            "Europa",
            "Afrika",
            "Asien",
            "Australien",
            "Antarktis");

	@PostConstruct
	public void init() {
		fishingNetList = fishingNetDatabase.getAllFishingNet();
		for (FishingNet net : fishingNetList) {
			System.out.println("Adding to view " + net);
			circlesView.add(net);
		}
	}
	
	public User getCurrentUser()
	{
		return this.currentUser;
	}
	
	public void setCurrentUser(User user)
	{
		this.currentUser = user;
	}
	
	public FishingNet getFishingNet() {
		return fishingNet;
	}

	public void setFishingNet(FishingNet fishingNet) {
		this.fishingNet = fishingNet;
	}

	public CirclesView getCirclesView() {
		return circlesView;
	}

	public List<FishingNet> getFishingNetList() {
		return fishingNetList;
	}

	public FishingNet getSelectedNet() {
		return selectedNet;
	}

	public void setSelectedNet(FishingNet selectedNet) {
		this.selectedNet = selectedNet;
	}
	
	public List<String> getContinents() {
        return CONTINENTS;
	}
	
	public List<FishingNet> getFishingNetsByContinent(String continent) 
	{
        return fishingNetList.stream()
                        .filter(n -> continent.equals(determineContinent(n.getLatitude(), n.getLongitude())))
                        .collect(Collectors.toList());
	}
		
	public boolean getShowMissing()
	{
		return this.showMissing;
	}
	
	public void setShowMissing(boolean missing)
	{
		this.showMissing = missing;
	}
	
	public boolean getShowName()
	{
		return this.showName;
	}
	
	public void setShowName(boolean name)
	{
		this.showName = name;
	}
	
	public boolean getShowNext()
	{
		return this.showNext;
	}
	
	public void setShowNext(boolean show)
	{
		this.showNext = show;
	}
	
	private String determineContinent(double lat, double lng) {
        if (lat < -60) {
                return "Antarktis";
        }
        if (lat >= 15 && lat <= 75 && lng >= -170 && lng <= -50) {
                return "Nordamerika";
        }
        if (lat >= -60 && lat < 15 && lng >= -90 && lng <= -30) {
                return "Suedamerika";
        }
        if (lat >= 35 && lat <= 72 && lng >= -10 && lng <= 60) {
                return "Europa";
        }
        if (lat >= -35 && lat <= 35 && lng >= -20 && lng <= 55) {
                return "Afrika";
        }
        if (lat >= -10 && lat <= 55 && lng > 55 && lng <= 180) {
                return "Asien";
        }
        return "Australien";
}

	public void onAddingNewCircle() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String shapeType = params.get("shapeType");

		if (!shapeType.equals("circle")) {
			System.out.println("Wrong type " + shapeType);
			return;
		}

		fishingNet.setLatitude(Double.parseDouble(params.get("centerLat")));
		fishingNet.setLongitude(Double.parseDouble(params.get("centerLng")));
		fishingNet.setRadius(Double.parseDouble(params.get("radius")));
		fishingNet.setStatus(FishingNet.NetStatus.REPORTED);
		fishingNet.setCreated(LocalDate.now());
	}
	
	public void addMessage(FacesMessage.Severity severity, String summary, String detail)
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public void saveNewFishingNet() {
		if (fishingNet.getLatitude() == null || fishingNet.getLongitude() == null || fishingNet.getRadius() == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte alle Felder ausfüllen.");
            return;
		}
		
		fishingNetList.add(fishingNet); // lokale Liste

		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		fishingNetDatabase.add(fishingNet);
		t.commit();
		addMessage(FacesMessage.SEVERITY_INFO, "Info", "Fischernetz wurde angelegt.");
		
		circlesView.add(fishingNet);
		fishingNet = new FishingNet(); // Formular zurücksetzen
	}

	public void processFishingNet()
	{
		if (selectedNet == null || this.currentUser == null)
		{
			System.out.println("nullptr");
			return;
		}
		
		if (this.currentUser.getName() == null || this.currentUser.getName().isBlank()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst einen Namen eingeben.");
            return;
		}
		if (this.currentUser.getMobile() == null || this.currentUser.getMobile().isBlank()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst eine Telefonnummer eingeben.");
            return;
		}
		
		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
	    User user = fishingNetDatabase.findUserByMobile(this.currentUser.getMobile());
		
		if (selectedNet.getEStatus() == FishingNet.NetStatus.REPORTED)
		{
		    if (user == null) 
		    {
	            user = currentUser;
	            fishingNetDatabase.addUser(user);
		    }
		    selectedNet.setUser(user);
		    selectedNet.setStatus(FishingNet.NetStatus.IN_PROGRESS);
		    fishingNetDatabase.update(selectedNet);
		    t.commit();
		    circlesView.update(selectedNet);
		    addMessage(FacesMessage.SEVERITY_INFO, "Info", "Fischernetz wurde in Arbeit gesetzt.");
		}
		else if (selectedNet.getEStatus() == FishingNet.NetStatus.IN_PROGRESS)
		{
			if (user != null)
			{
				if (selectedNet.getUser().getMobile().equals(user.getMobile()))
				{
					selectedNet.setUser(user);
					selectedNet.setStatus(FishingNet.NetStatus.SECURED);
					fishingNetDatabase.update(selectedNet);
				    t.commit();
				    circlesView.update(selectedNet);
				    addMessage(FacesMessage.SEVERITY_INFO, "Info", "Fischernetz wurde gesichert.");
				}
				else
				{
					addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Die eingegebene Nummer stimmt nicht mit der erwarteten Nummer überein.");
				}
			}
			else
			{
				addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Die eingegebene Nummer existiert nicht.");
			}
			
		}
		
		currentUser = new User();
	}
	
	public void cancelFishingNet()
	{
		if (selectedNet == null || this.currentUser == null)
		{
			return;
		}
		if ( this.currentUser.getMobile() == null || this.currentUser.getMobile().isBlank()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst eine Telefonnummer eingeben.");
            return;
		}
		
		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
	    User user = fishingNetDatabase.findUserByMobile(this.currentUser.getMobile());
	    
	    if (user != null)
	    {
	    	if (selectedNet.getUser().getMobile().equals(user.getMobile()))
	    	{
	    		selectedNet.setUser(user);
		    	selectedNet.setStatus(FishingNet.NetStatus.LOST);
				fishingNetDatabase.update(selectedNet);
			    t.commit();	
			    circlesView.update(selectedNet);
			    addMessage(FacesMessage.SEVERITY_INFO, "Info", "Fischernetz wurde als Verschollen gemeldet.");
	    	}
	    	else
	    	{
	    		addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Die eingegebene Nummer stimmt nicht mit der erwarteten Nummer überein.");
	    	}
	    }
	    else
	    {
	    	addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Die eingegebene Nummer existiert nicht.");
	    }
	    currentUser = new User();
	}

	public void circleSelected(OverlaySelectEvent<Long> event) {
		try {
			Circle<Long> overlay = (Circle<Long>) event.getOverlay();
			if (overlay != null) {
				for(FishingNet net : fishingNetList)
				{
					if (Long.valueOf(net.getId()) == overlay.getData())
					{
						selectedNet = net;
						
						if (selectedNet.getEStatus() == FishingNet.NetStatus.REPORTED)
							showName = true;
						else
							showName = false;
						
						// Vermisst kann nur ausgewählt werden, wenn es bereits 'in Arbeit' ist. 
						if (selectedNet.getEStatus() == FishingNet.NetStatus.IN_PROGRESS)
							showMissing = true;
						else
							showMissing = false;
						
						if (selectedNet.getEStatus() != FishingNet.NetStatus.LOST && selectedNet.getEStatus() != FishingNet.NetStatus.SECURED)
							showNext = true;
						else				
							showNext = false;
						
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}