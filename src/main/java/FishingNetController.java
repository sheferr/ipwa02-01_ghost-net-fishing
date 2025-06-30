import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.*;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityTransaction;

@Named
//@RequestScoped
@ViewScoped
public class FishingNetController implements Serializable {
	
	@Inject
	NetDataBase fishingNetDatabase;
	
	private FishingNet fishingNet = new FishingNet();
	private List<FishingNet> fishingNetList = new ArrayList<>();
	private MapModel<Long> circleModel;
	
	public FishingNet getFishingNet()
	{
		return fishingNet;
	}
	
	public void setFishingNet(FishingNet fishingNet)
	{
		this.fishingNet = fishingNet;
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
		System.out.println("Typ: " + shapeType + ", Lat: " + fishingNet.getLatitude() + ", Lng: " + fishingNet.getLongitude() + " radius: " + fishingNet.getRadius());
	}
	
	public MapModel<Long> getCircleModel()
	{
		return circleModel;
	}
	
	public void saveNewFishingNet() {
		System.out.println("Saving new fishing net: " + fishingNet);
		fishingNetList.add(fishingNet); // lokale Liste
		
		EntityTransaction t = fishingNetDatabase.getAndBeginTransaction();
		fishingNetDatabase.add(fishingNet);
        t.commit();
				
		System.out.println("Neues Netz gespeichert: lat: " + fishingNet.getLatitude() + ", lng: " + fishingNet.getLongitude()
							+ ", radius: " + fishingNet.getRadius() + ", status: " + fishingNet.getStatus() + ", created: " + fishingNet.getCreated());
		
        fishingNet = new FishingNet(); // Formular zurücksetzen
	}
	
	public void onBlurAction()
	{
		System.out.println("change here longitude" + fishingNet.getLongitude());
	}
	
	public void circleSelected(OverlaySelectEvent<Long> event) {
		Overlay<Long> overlay = event.getOverlay();
		System.out.println(overlay);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Circle " + overlay.getData() + " Selected", null));
	}
}