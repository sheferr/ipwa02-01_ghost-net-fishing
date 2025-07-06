import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntToLongFunction;
import java.util.function.LongToIntFunction;
import java.util.function.ToLongFunction;

import org.primefaces.PrimeFaces;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.*;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
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

	@PostConstruct
	public void init() {
		fishingNetList = fishingNetDatabase.getAllFishingNet();
		System.out.println("Eingelesene Fischernetze: " + fishingNetList.size());

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

		System.out.println("JavaScript hat ein Shape gezeichnet:");
		System.out.println("Typ: " + shapeType + ", Lat: " + fishingNet.getLatitude() + ", Lng: "
				+ fishingNet.getLongitude() + " radius: " + fishingNet.getRadius());
	}

	public void saveNewFishingNet() {
		System.out.println("Saving new fishing net: " + fishingNet);
		fishingNetList.add(fishingNet); // lokale Liste

		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		fishingNetDatabase.add(fishingNet);
		t.commit();

		System.out.println("Neues Netz gespeichert: lat: " + fishingNet.getLatitude() + ", lng: "
				+ fishingNet.getLongitude() + ", radius: " + fishingNet.getRadius() + ", status: "
				+ fishingNet.getStatus() + ", created: " + fishingNet.getCreated());

		circlesView.add(fishingNet);
		fishingNet = new FishingNet(); // Formular zurücksetzen
	}

	public void circleSelected(OverlaySelectEvent<Long> event) {
		try {
			Circle<Long> overlay = (Circle<Long>) event.getOverlay();
			System.out.println("Overlay selected: " + overlay);
			if (overlay != null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Circle " + overlay.getData() + " Selected", null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void onBlurAction() {
		System.out.println("Aktualisiert!");
	}
}