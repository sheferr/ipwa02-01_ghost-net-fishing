import java.io.Serializable;
import java.util.Map;

import org.primefaces.event.map.*;
import org.primefaces.model.map.*;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;

@Entity
@Named
@ViewScoped
public class EventView implements Serializable {

    public void onStateChange(StateChangeEvent event) {
        LatLngBounds bounds = event.getBounds();

        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Zoom Level", String.valueOf(event.getZoomLevel())));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Center", event.getCenter().toString()));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "NorthEast", bounds.getNorthEast().toString()));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "SouthWest", bounds.getSouthWest().toString()));
    }

    public void onPointSelect(PointSelectEvent event) {
        LatLng latlng = event.getLatLng();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Point Selected",
                "Lat:" + latlng.getLat() + ", Lng:" + latlng.getLng()));
    }

    public void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void onStateChangeFromJS() {
    	Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();

        String shapeType = params.get("shapeType");
        String lat = params.get("centerLat");
        String lng = params.get("centerLng");
        String radius = params.get("radius");

        System.out.println("JavaScript hat ein Shape gezeichnet:");
        System.out.println("Typ: " + shapeType + ", Lat: " + lat + ", Lng: " + lng + "radius: "+ radius);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Empfangen", shapeType + " bei (" + lat + ", " + lng + ") radius: "+ radius +""));
    	
    	
    	//addMessage(new FacesMessage("onStateChangeFromJS wurde aufgerufen!"));
        /*System.out.println("onStateChangeFromJS wurde aufgerufen!");
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Aufruf aus JS", "RemoteCommand funktioniert!"));*/
    }
}