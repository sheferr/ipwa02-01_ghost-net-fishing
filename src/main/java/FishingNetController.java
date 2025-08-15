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
	private static final long serialVersionUID = 1L;
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
		"Südlicher Ozean",
	    "Mittelmeer",
	    "Nordsee",
	    "Ostsee",
	    "Karibisches Meer",
	    "Arabisches Meer",
	    "Golf von Bengalen",
	    "Arktischer Ozean",
	    "Indischer Ozean",
	    "Nordatlantik",
	    "Südatlantik",
	    "Nordpazifik",
	    "Südpazifik",
	    "Offener Ozean"
	    );


	@PostConstruct
	public void init() {
		fishingNetList = fishingNetDatabase.getAllFishingNet();
		for (FishingNet net : fishingNetList) {
			System.out.println("Adding to view " + net);
			circlesView.add(net);
		}
	}

	public User getCurrentUser() {
		return this.currentUser;
	}

	public void setCurrentUser(User user) {
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
		
		showName = showNameInput(this.selectedNet.getEStatus());
		
		// Vermisst kann nur ausgewählt werden, wenn es bereits 'in Arbeit' ist.
		showMissing = showMissingButton(this.selectedNet.getEStatus());

		showNext = showNextStepButton(this.selectedNet.getEStatus());
	}

	public List<String> getContinents() {
		return CONTINENTS;
	}

	public List<FishingNet> getFishingNetsByContinent(String continent) {
		return fishingNetList.stream()
				.filter(n -> continent.equals(determineWaterBody(n.getLatitude(), n.getLongitude())))
				.collect(Collectors.toList());
	}

	public boolean getShowMissing() {
		return this.showMissing;
	}

	public void setShowMissing(boolean missing) {
		this.showMissing = missing;
	}

	public boolean getShowName() {
		return this.showName;
	}

	public void setShowName(boolean name) {
		this.showName = name;
	}

	public boolean getShowNext() {
		return this.showNext;
	}

	public void setShowNext(boolean show) {
		this.showNext = show;
		
	}

	private String determineWaterBody(double lat, double lng) {
	    // Normalisiere Längengrad auf [-180, 180]
	    double lon = ((lng + 180) % 360 + 360) % 360 - 180;

	    // Polarregionen
	    if (lat <= -50) return "Südlicher Ozean";
	    if (lat >= 66.5) return "Arktischer Ozean";

	    // Bekannte Randmeere
	    if (lat >= 30 && lat <= 46 && lon >= -6.5 && lon <= 37) return "Mittelmeer";
	    if (lat >= 51 && lat <= 62 && lon >= -5 && lon <= 9) return "Nordsee";
	    if (lat >= 53 && lat <= 66 && lon >= 9 && lon <= 31.5) return "Ostsee";
	    if (lat >= 9 && lat <= 27 && lon >= -90 && lon <= -60) return "Karibisches Meer";
	    if (lat >= 0 && lat <= 25 && lon >= 50 && lon <= 75) return "Arabisches Meer";
	    if (lat >= 5 && lat <= 23 && lon >= 80 && lon <= 100) return "Golf von Bengalen";

	    // Große Ozeane als Fallback
	    if (lon >= 20 && lon <= 146 && lat > -50 && lat < 30) return "Indischer Ozean";
	    if (lon >= -100 && lon <= 20) return (lat >= 0) ? "Nordatlantik" : "Südatlantik";
	    if (lon >= 146 || lon <= -100) return (lat >= 0) ? "Nordpazifik" : "Südpazifik";

	    return "Offener Ozean";
	}

	public void onAddingNewCircle() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		fishingNet.setLatitude(Double.parseDouble(params.get("centerLat")));
		fishingNet.setLongitude(Double.parseDouble(params.get("centerLng")));
		fishingNet.setRadius(Double.parseDouble(params.get("radius")));
		fishingNet.setStatus(FishingNet.NetStatus.REPORTED);
		fishingNet.setCreated(LocalDate.now());
	}

	public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
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

	private void reportedFishingNet() {
		if (this.currentUser.getName() == null || this.currentUser.getName().isBlank()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst einen Namen eingeben.");
			return;
		}
		if (this.currentUser.getMobile() == null || this.currentUser.getMobile().isBlank()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst eine Telefonnummer eingeben.");
			return;
		}

		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		User user = fishingNetDatabase.findUser(this.currentUser);

		if (user == null) {
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

	private void progressFishingNet() {
		if (this.currentUser.getMobile() == null || this.currentUser.getMobile().isBlank()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst eine Telefonnummer eingeben.");
			return;
		}

		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		User user = fishingNetDatabase.findUserByMobile(this.currentUser.getMobile());

		if (user != null) {
			if (selectedNet.getUser().getMobile().equals(user.getMobile())) {
				selectedNet.setUser(user);
				selectedNet.setStatus(FishingNet.NetStatus.SECURED);
				fishingNetDatabase.update(selectedNet);
				t.commit();
				circlesView.update(selectedNet);
				addMessage(FacesMessage.SEVERITY_INFO, "Info", "Fischernetz wurde gesichert.");
			} else {
				addMessage(FacesMessage.SEVERITY_ERROR, "Fehler",
						"Die eingegebene Nummer stimmt nicht mit der erwarteten Nummer überein.");
			}
		} else {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Die eingegebene Nummer existiert nicht.");
		}
	}

	public void processFishingNet() {
		if (selectedNet == null || this.currentUser == null) {
			System.out.println("nullptr");
			return;
		}

		switch (selectedNet.getEStatus()) {
		case FishingNet.NetStatus.REPORTED:
			reportedFishingNet();
			break;
		case FishingNet.NetStatus.IN_PROGRESS:
			progressFishingNet();
			break;
		default:
			System.out.println("Unknown status call");
		}

		currentUser = new User();
	}

	public void cancelFishingNet() {
		if (selectedNet == null || this.currentUser == null) {
			return;
		}
		if (this.currentUser.getMobile() == null || this.currentUser.getMobile().isBlank()) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte zuerst eine Telefonnummer eingeben.");
			return;
		}

		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		User user = fishingNetDatabase.findUserByMobile(this.currentUser.getMobile());

		if (user != null) {
			if (selectedNet.getUser().getMobile().equals(user.getMobile())) {
				selectedNet.setUser(user);
				selectedNet.setStatus(FishingNet.NetStatus.LOST);
				fishingNetDatabase.update(selectedNet);
				t.commit();
				circlesView.update(selectedNet);
				addMessage(FacesMessage.SEVERITY_INFO, "Info", "Fischernetz wurde als Verschollen gemeldet.");
			} else {
				addMessage(FacesMessage.SEVERITY_ERROR, "Fehler",
						"Die eingegebene Nummer stimmt nicht mit der erwarteten Nummer überein.");
			}
		} else {
			addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Die eingegebene Nummer existiert nicht.");
		}
		currentUser = new User();
	}
			
	public void circleSelected(OverlaySelectEvent<Long> event) {
		try {
			Circle<Long> overlay = (Circle<Long>) event.getOverlay();
			if (overlay != null) {
				for (FishingNet net : fishingNetList) {
					if (Long.valueOf(net.getId()) == overlay.getData()) {
						setSelectedNet(net);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private boolean showNameInput(FishingNet.NetStatus status)
	{
		return (status == FishingNet.NetStatus.REPORTED);
	}
	
	private boolean showMissingButton(FishingNet.NetStatus status)
	{
		return (status == FishingNet.NetStatus.IN_PROGRESS);
	}
	
	private boolean showNextStepButton(FishingNet.NetStatus status)
	{
		return (status != FishingNet.NetStatus.LOST && 
				status != FishingNet.NetStatus.SECURED);
	}
	
	
}