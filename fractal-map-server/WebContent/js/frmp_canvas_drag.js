FRMP.dragStartX = 0;
FRMP.dragStartY = 0;
FRMP.targetX = 0;
FRMP.targetY = 0;
FRMP.dragImageData = null;

FRMP.mouseDownListener = function(evt) {
	console.log('mouse down fired');
	var rect = FRMP.fractalCanvas.getBoundingClientRect();
	FRMP.dragStartX = evt.clientX - rect.left;
	FRMP.dragStartY = evt.clientY - rect.top;
	var canvas = FRMP.fractalCanvas;
	
	
	window.addEventListener("mousemove", FRMP.mouseMoveListener, false);
	FRMP.fractalCanvas.removeEventListener("mousedown", FRMP.mouseDownListener, false);
	window.addEventListener("mouseup", FRMP.mouseUpListener, false);
	
	evt.preventDefault();
	return false;
};

FRMP.mouseMoveListener = function(evt) {
	var rect = FRMP.fractalCanvas.getBoundingClientRect();
	mouseX = evt.clientX - rect.left;
	mouseY = evt.clientY - rect.top;
	
	var posX;
	var posY;
	var minX = 0;
	var maxX = FRMP.fractalCanvas.width;
	var minY = 0;
	var maxY = FRMP.fractalCanvas.height;
	
	//clamp x and y positions to prevent object from dragging outside of canvas
	posX = mouseX;
	posX = (posX < minX) ? minX : ((posX > maxX) ? maxX : posX);
	posY = mouseY;
	posY = (posY < minY) ? minY : ((posY > maxY) ? maxY : posY);
	
	FRMP.targetX = posX;
	FRMP.targetY = posY;
	
	var shiftX = FRMP.targetX - FRMP.dragStartX;
	var shiftY = FRMP.targetY - FRMP.dragStartY;
	var canvas = FRMP.fractalCanvas;
	var ctx = canvas.getContext('2d');
	if (FRMP.dragImageData === null && (Math.abs(shiftX) > 10 || Math.abs(shiftY) > 10)) {
		FRMP.dragImageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
	}
	if (FRMP.dragImageData !== null) {
		FRMP.clearCanvas();
		ctx.putImageData(FRMP.dragImageData, shiftX, shiftY);
	}
};

FRMP.mouseUpListener = function(evt) {
	window.removeEventListener("mouseup", FRMP.mouseUpListener, false);
	window.removeEventListener("mousemove", FRMP.mouseMoveListener, false);
	FRMP.dragImageData = null;
	var shiftX = FRMP.targetX - FRMP.dragStartX;
	var shiftY = FRMP.targetY - FRMP.dragStartY;
	if (Math.abs(shiftX) > 10 || Math.abs(shiftY) > 10) {
		FRMP.moveViewport(shiftX, shiftY);
	}
	FRMP.fractalCanvas.addEventListener("mousedown", FRMP.mouseDownListener, false);
};