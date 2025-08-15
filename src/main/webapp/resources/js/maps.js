(function () {
  var tries = 0;

  function initMap() {
    var w = window.PF ? PF('myMap') : null;
    if (!w || typeof w.getMap !== 'function') {
      return defer();
    }
    if (!(window.google && google.maps && google.maps.drawing)) {
      return defer();
    }

    var map = w.getMap();
    if (!map) {
      return defer();
    }
	
    var drawingManager = new google.maps.drawing.DrawingManager({
      drawingControl: true,
      drawingControlOptions: { 
		position: google.maps.ControlPosition.TOP_CENTER,
		drawingModes: [google.maps.drawing.OverlayType.CIRCLE] 
		}
    });
    drawingManager.setMap(map);

    google.maps.event.addListener(drawingManager, 'circlecomplete', function (circle) {
		console.log('Overlay completed:', circle);
      try {
        callOnStateChange([
          { name: 'centerLat',  value: circle.getCenter().lat() },
          { name: 'centerLng',  value: circle.getCenter().lng() },
          { name: 'radius',     value: circle.getRadius() }
        ]);
      } catch (e) {
        console.error('callOnStateChange fehlgeschlagen', e);
      }
    });
  }

  function defer() {
    if (++tries > 200) {  // ~20s Timeout
      console.error('Google Maps oder PF-Widget nicht verfügbar');
      return;
    }
    setTimeout(initMap, 100);
  }

  if (window.PrimeFaces && PrimeFaces.onReady) {
    PrimeFaces.onReady(initMap);
  } else {
    document.addEventListener('DOMContentLoaded', initMap);
  }
})();
