$(document).ready(function() {
	FRMP.showStatus('Page loaded');
	
	$('#home_button').button().click(FRMP.returnToInitialState);
	
	var canvas = $('#fractal_canvas')[0];
	canvas.width = window.innerWidth - 60;
	canvas.height = window.innerHeight - 60;
	FRMP.fractalCanvas = $('#fractal_canvas');
	FRMP.initializeForm();
	
	FRMP.fractalCanvas.on('click', onCanvasClick);
	FRMP.fractalCanvas.on('dblclick', onCanvasDblClick);
	// $(canvas).mousewheel(onCanvasMouseWheel);
	// not jQuery drag realization
	// canvas.addEventListener('mousedown', FRMP.mouseDownListener, false);
});

function refreshPageInfo() {
	$('#point_re').val(FRMP.centerRe.toString());
	$('#point_im').val(FRMP.centerIm.toString());
	$('#layer_index').text('Layer index: ' + FRMP.currentLayerIndex);
}

function onCanvasClick(evt) {
	console.log('mouse clicked');
	var canvasCoords = getClickCoordsInCanvas(evt);
	FRMP.getPointCoords(canvasCoords.canvasX, canvasCoords.canvasY);
	return false;
}

function getClickCoordsInCanvas(evt) {
	var rect = FRMP.fractalCanvas[0].getBoundingClientRect();
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
	return false;
}

function onCanvasMouseWheel(evt, delta) {
	$('#fractal_canvas').unmousewheel();
	if (delta < 0) {
		FRMP.zoomOutOneLayer();
	} else {
		FRMP.zoomInOneLayer();
	}
	$('#fractal_canvas').mousewheel(onCanvasMouseWheel);
	return false;
}