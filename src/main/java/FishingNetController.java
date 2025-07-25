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
	private FishingNet selectedNet;
	private String mobile;
	
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
		System.out.println("Eingelesene Fischernetze: " + fishingNetList.size());
		System.out.println("size of continent list: " + CONTINENTS.size());

		for (FishingNet net : fishingNetList) {
			System.out.println("Adding net to view..");
			circlesView.add(net);
		}
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
	
	public String getMobile()
	{
		return this.mobile;
	}
	
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
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

	public void saveNewFishingNet() {
		fishingNetList.add(fishingNet); // lokale Liste

		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		fishingNetDatabase.add(fishingNet);
		t.commit();
		
		System.out.println("Save new Fishing net" + fishingNet);

		circlesView.add(fishingNet);
		fishingNet = new FishingNet(); // Formular zurücksetzen
	}
	
	public void processFishingNet()
	{
		if (selectedNet == null || mobile == null || mobile.isBlank()) {
            return;
		}
	    EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
	    User user = fishingNetDatabase.findUserByMobile(mobile);
	    if (user == null) 
	    {
            user = new User();
            user.setMobile(mobile);
            fishingNetDatabase.addUser(user);
	    }
	    selectedNet.setUser(user);
	    selectedNet.setStatus(FishingNet.NetStatus.IN_PROGRESS);
	    fishingNetDatabase.update(selectedNet);
	    t.commit();
	    mobile = "";
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
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void onBlurAction() {
	}
}