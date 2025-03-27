var drawingManager;
var map;

function initializeDrawingManager() {
    map = PF('gmap').getMap(); // Zugriff auf die PrimeFaces Google Map

    drawingManager = new google.maps.drawing.DrawingManager({
        drawingMode: google.maps.drawing.OverlayType.MARKER,
        drawingControl: true,
        drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_CENTER,
            drawingModes: ['marker', 'circle', 'polygon', 'polyline', 'rectangle']
        },
        markerOptions: { draggable: true }
    });

    drawingManager.setMap(map);
}