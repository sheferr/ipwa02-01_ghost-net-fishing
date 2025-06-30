import java.io.Serializable;

import org.primefaces.event.map.*;
import org.primefaces.model.map.*;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;

public class CirclesView implements Serializable {

    private MapModel<Long> circleModel;

    @PostConstruct
    public void init() {

        circleModel = new DefaultMapModel<>();

        //Shared coordinates
        LatLng coord1 = new LatLng(36.879466, 30.667648);
        LatLng coord4 = new LatLng(36.885233, 30.702323);

        //Circle
        Circle<Long> circle1 = new Circle<>(coord1, 500);
        circle1.setStrokeColor("#d93c3c");
        circle1.setFillColor("#d93c3c");
        circle1.setFillOpacity(0.5);
        circle1.setData(1L);

        Circle<Long> circle2 = new Circle<>(coord4, 300);
        circle2.setStrokeColor("#00ff00");
        circle2.setFillColor("#00ff00");
        circle2.setStrokeOpacity(0.7);
        circle2.setFillOpacity(0.7);
        circle2.setData(2L);
        
        circleModel.addOverlay(circle1);
        circleModel.addOverlay(circle2);
        
    }

    public MapModel<Long> getCircleModel() {
        return circleModel;
    }

    public void onCircleSelect(OverlaySelectEvent<Long> event) {
        Overlay<Long> overlay = event.getOverlay();
                
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Circle " + overlay.getData() + " Selected", null));
    }
}