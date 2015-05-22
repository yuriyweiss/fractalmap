FRMP.initializeForm = function() {
	$.getJSON('initialize-form', function(initParams) {
		FRMP.centerRe = initParams.centerRe;
		FRMP.centerIm = initParams.centerIm;
		FRMP.currentLayerIndex = initParams.currentLayerIndex;
		refreshPageInfo();
		FRMP.squareSideSize = initParams.squareSideSize;
		FRMP.layers = initParams.layers;
		FRMP.showStatus('SUCCESS Form initialized');
		FRMP.loadSquares();
		// FRMP.fractalCanvas.addEventListener("mousedown", FRMP.mouseDownListener, false);
	});
};

FRMP.loadSquares = function() {
	var data = {
		areaWidth : FRMP.fractalCanvas.width,
		areaHeight : FRMP.fractalCanvas.height
	};
	$.getJSON('squares-partition', data, function(partitionResult) {
		if (partitionResult.wasError) {
			FRMP.showStatus('ERROR loading squares partition: '
					+ partitionResult.errorMessage);
		} else {
			FRMP.showStatus('SUCCESS Area partition squares count: '
					+ partitionResult.squares.length);
			FRMP.squares = partitionResult.squares;
			FRMP.clearCanvas();
			FRMP.processingSquare = 0;
			FRMP.loadNextSquare();
		}
	});
};

FRMP.clearCanvas = function() {
	var canvas = FRMP.fractalCanvas;
	var ctx = canvas.getContext('2d');
	ctx.fillStyle = "rgb(255, 255, 255)";
	ctx.fillRect(0, 0, canvas.width, canvas.height);
};

FRMP.loadNextSquare = function() {
	// Stop recursion when last square processed.
	if ((FRMP.squares.length - 1) < FRMP.processingSquare) {
		return;
	}
	// Prepare ajax request.
	var square = FRMP.squares[FRMP.processingSquare];
	FRMP.currentSquare = square;
	FRMP.showStatus("current square: " + square.leftRe + ", " + square.topIm
			+ ", " + square.canvasLeftX + ", " + square.canvasTopY);
	var data = {
		leftRe : square.leftRe,
		topIm : square.topIm
	};
	// Perform request, wait for response.
	$.getJSON('get-square', data, function(squareResult) {
		if (FRMP.isArray(squareResult)) {
			FRMP.showStatus('SUCCESS square body loaded: [leftRe: '
					+ FRMP.currentSquare.leftRe + ', topIm:'
					+ FRMP.currentSquare.topIm + ']');
			FRMP.paintSquare(squareResult);
		} else if (squareResult.wasError) {
			FRMP.showStatus('ERROR loading square: '
					+ squareResult.errorMessage);
		} else {
			FRMP.showStatus('SUCCESS square iterations loaded: [leftRe: '
					+ FRMP.currentSquare.leftRe + ', topIm:'
					+ FRMP.currentSquare.topIm + ', iterations: '
					+ squareResult.iterations + ']');
			FRMP.paintSquare(squareResult);
		}
		// Increase processing square index and call next ajax request.
		FRMP.processingSquare++;
		FRMP.loadNextSquare();
	});
};

FRMP.getPointCoords = function(canvasX, canvasY) {
	// get canvas center coords
	var canvasCenterX = Math.round(FRMP.fractalCanvas.width / 2);
	var canvasCenterY = Math.round(FRMP.fractalCanvas.height / 2);
	// calculate shiftX, shiftY relative to the center
	var calcShiftX = canvasX - canvasCenterX;
	// y coord shift is inverted
	var calcShiftY = canvasCenterY - canvasY;
	var data = {
		shiftX : calcShiftX,
		shiftY : calcShiftY
	};
	$.getJSON('get-point-coords', data, function(coordsResult) {
		if (!coordsResult.wasError) {
			$('#point_re').val(coordsResult.re.toString());
			FRMP.mousePosRe = coordsResult.re;
			$('#point_im').val(coordsResult.im.toString());
			FRMP.mousePosIm = coordsResult.im;
		}
	});
};

FRMP.zoomInOneLayer = function(canvasX, canvasY) {
	FRMP.zoomOneLayer(true, canvasX, canvasY);
};

FRMP.zoomOneLayer = function(zoomIn, canvasX, canvasY) {
	var zoomOut = !zoomIn;
	// return if no zoom possible
	if ((zoomIn && !FRMP.hasNextLayer()) || (zoomOut && !FRMP.hasPrevLayer())) {
		console.log('Zoom impossible. Side layer reached.');
		return;
	}
	// get zoom base point coords
	var pointRe = FRMP.centerRe;
	var pointIm = FRMP.centerIm;
	if (canvasX && canvasY) {
		FRMP.getPointCoords(canvasX, canvasY);
		pointRe = FRMP.mousePosRe;
		pointIm = FRMP.mousePosIm;
	}
	// get target layer index
	var changedLayerIndex = FRMP.getNextLayerIndex();
	if (zoomOut) {
		changedLayerIndex = FRMP.getPrevLayerIndex();
	}
	// change layer, then load new squares
	var data = {
		layerIndex : changedLayerIndex,
		re : pointRe,
		im : pointIm
	}
	$.get('change-layer', data, function() {
		FRMP.loadSquares();
		FRMP.centerRe = pointRe;
		FRMP.centerIm = pointIm;
		FRMP.currentLayerIndex = changedLayerIndex;
		refreshPageInfo();
	});
};

FRMP.zoomOutOneLayer = function() {
	FRMP.zoomOneLayer(false);
};