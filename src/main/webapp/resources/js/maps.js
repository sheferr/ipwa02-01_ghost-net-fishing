
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
		drawingMode: google.maps.drawing.OverlayType.MARKER,
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
		alert('Shape drawn on map!');
	});

	return;
}