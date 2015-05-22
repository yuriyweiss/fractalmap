FRMP.dragStartX = 0;
FRMP.dragStartY = 0;
FRMP.targetX = 0;
FRMP.targetY = 0;

FRMP.mouseDownListener = function(evt) {
	var rect = FRMP.fractalCanvas.getBoundingClientRect();
	FRMP.dragStartX = evt.clientX - rect.left;
	FRMP.dragStartY = evt.clientY - rect.top;
	
	window.addEventListener("mousemove", FRMP.mouseMoveListener, false);
	FRMP.fractalCanvas.removeEventListener("mousedown", FRMP.mouseDownListener, false);
	window.addEventListener("mouseup", FRMP.mouseUpListener, false);
	
	event.prevenDefault();
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
	posX = mouseX - dragHoldX;
	posX = (posX < minX) ? minX : ((posX > maxX) ? maxX : posX);
	posY = mouseY - dragHoldY;
	posY = (posY < minY) ? minY : ((posY > maxY) ? maxY : posY);
	
	targetX = posX;
	targetY = posY;
	
	// TODO redraw canvas
	
	// INFO copy and paste canvas region
	// http://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_canvas_getimagedata
};

FRMP.mouseUpListener = function(evt) {
	FRMP.fractalCanvas.addEventListener("mousedown", FRMP.mouseDownListener, false);
	window.removeEventListener("mouseup", FRMP.mouseUpListener, false);
	window.removeEventListener("mousemove", FRMP.mouseMoveListener, false);
};