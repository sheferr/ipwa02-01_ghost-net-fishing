
function initMap() {
	console.log("Starting to init a map");
	if (typeof google === "undefined" || typeof google.maps === "undefined") {
		console.log("Google Maps API not yet loaded. Retrying...");
	}

	if (typeof PF === "undefined" || !PF('myMap') || !PF('myMap').getMap()) {
		console.log("PrimeFaces map not ready. Retrying...");
		setTimeout(initMap, 1000);
		return;
	}
	console.log("PrimeFaces map ready.");

	var gmap = PF('myMap').getMap();
	var drawingManager = new google.maps.drawing.DrawingManager({
		drawingMode: null,
		drawingControl: true,
		drawingControlOptions: {
			position: google.maps.ControlPosition.TOP_CENTER,
			drawingModes: [
				google.maps.drawing.OverlayType.CIRCLE,
			]
		}
	});

	drawingManager.setMap(gmap);

	google.maps.event.addListener(drawingManager, 'overlaycomplete', function(event) {
		console.log('Overlay completed:', event);
		
		let shapeType = event.type; // e.g., "circle"
		let center = event.overlay.getCenter ? event.overlay.getCenter() : null;
		let radius = event.overlay.radius ? event.overlay.getRadius() : null;
		
		if (center) {
		callOnStateChange([
			{ name: 'shapeType', value: shapeType },
			{ name: 'centerLat', value: center.lat() },
			{ name: 'centerLng', value: center.lng() },
			{ name: 'radius', value: radius }
		]);
		} else {
			callOnStateChange([
				{ name: 'shapeType', value: shapeType }
			]);
		}
		
	});
	
	

	return;
}