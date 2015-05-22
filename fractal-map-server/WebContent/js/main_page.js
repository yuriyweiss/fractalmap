$(document).ready(function() {
	FRMP.showStatus('Page loaded');
	var canvas = document.getElementById('fractal_canvas');
	FRMP.fractalCanvas = canvas;
	canvas.width = window.innerWidth - 60;
	canvas.height = window.innerHeight - 60;
	FRMP.initializeForm();
	
	canvas.addEventListener('click', onCanvasClick, false);
	canvas.addEventListener('dblclick', onCanvasDblClick, false);
});

function onCanvasClick(evt) {
	console.log('mouse clicked');
	var canvasCoords = getClickCoordsInCanvas(evt);
	FRMP.getPointCoords(canvasCoords.canvasX, canvasCoords.canvasY);
}

function getClickCoordsInCanvas(evt) {
	var rect = FRMP.fractalCanvas.getBoundingClientRect();
	var result = {
		canvasX : evt.clientX - rect.left,
		canvasY : evt.clientY - rect.top
	}
	return result;
}

function onCanvasDblClick(evt) {
	console.log('mouse dblclicked');
	var canvasCoords = getClickCoordsInCanvas(evt);
	FRMP.zoomInOneLayer(canvasCoords.canvasX, canvasCoords.canvasY);
}