FRMP.paintSquare = function(squareResult) {
	FRMP.showStatus('Painting square');
	if (FRMP.isArray(squareResult)) {
		FRMP.showStatus('Paint square body');
		FRMP.paintSquareIterationsDiffer(squareResult);
	} else {
		FRMP.showStatus('Paint square iterations: ' + sqaureResult.iterations);
		FRMP.paintSquareIterationsCommon(squareResult.iterations);
	}
};

FRMP.paintSquareIterationsCommon = function(iterations) {
	 var colorValue = FRMP.getColorValue(iterations);
	 var canvas = $('#fractal_canvas');
	 var ctx = canvas.getContext('2d');
	 ctx.fillStyle = rgb(colorValue, colorValue, colorValue);
	 ctx.fillRect(FRMP.currentSquare.canvasLeftX, FRMP.currentSquare.canvasTopY, FRMP.squareSideSize, FRMP.squareSideSize);
};

FRMP.getColorValue = function(iterations) {
	if (iterations == -1)
		return 0;
	var result = (iterations % 16) + 1;
	result = (result * 16) - 1;
	return result;
};

FRMP.paintSquareIterationsDiffer = function(squareBody) {
	var canvas = FRMP.fractalCanvas;
	var square = FRMP.currentSquare;
	var ctx = canvas.getContext('2d');
	var inverted = (square.topIm <= 0) ? true : false;
	FRMP.showStatus('Square inverted: ' + inverted + ' leftRe: '
			+ square.leftRe + ' topIm: ' + square.topIm + ' canvasLeftX: '
			+ square.canvasLeftX + ' canvasTopY: ' + square.canvasTopY);
	var imgData = ctx.createImageData(FRMP.squareSideSize, FRMP.squareSideSize);
	for (var i = 0; i < FRMP.squareSideSize; i++) {
		for (var j = 0; j < FRMP.squareSideSize; j++) {
			var targetI = (inverted) ? ((FRMP.squareSideSize - 1) - i) : i;
			var dataIndex = ((i * FRMP.squareSideSize) + j) * 4;
			var colorValue = FRMP.getColorValue(squareBody[targetI][j]);
			imgData.data[dataIndex + 0] = colorValue;
			imgData.data[dataIndex + 1] = colorValue;
			imgData.data[dataIndex + 2] = colorValue;
			imgData.data[dataIndex + 3] = 255; // alpha
		}
	}
	ctx.putImageData(imgData, square.canvasLeftX, square.canvasTopY);
};