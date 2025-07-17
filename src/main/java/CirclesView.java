import java.io.Serializable;
import java.util.List;
import org.primefaces.model.map.*;

import jakarta.annotation.PostConstruct;

public class CirclesView implements Serializable {

	private MapModel<Long> circleModel = new DefaultMapModel<Long>();

	@PostConstruct
	public void init() {
		System.out.println("Init function CirclesView");
	}

	private Circle<Long> addNewCircle(FishingNet net) {
		Circle<Long> circle = new Circle<>(new LatLng(net.getLatitude(), net.getLongitude()), net.getRadius());

		circle.setStrokeColor(net.getEStatus().getStrokeColor());
		circle.setFillColor(net.getEStatus().getFillColor());
		circle.setFillOpacity(0.5);
		System.out.println("ID is: " + net.getId());
		circle.setData(Long.valueOf(net.getId()));
		return circle;
	}

	public MapModel<Long> getCircleModel() {
		return circleModel;
	}

	public void add(FishingNet net) {
		add(addNewCircle(net));
	}

	public void add(Circle<Long> circle) {
		if (circle != null) {
			circleModel.addOverlay(circle);
			System.out.println("Circle added: " + circle.getId());
		}
	}

	public List<Circle<Long>> getAllCircles() {
		return circleModel.getCircles();
	}

}