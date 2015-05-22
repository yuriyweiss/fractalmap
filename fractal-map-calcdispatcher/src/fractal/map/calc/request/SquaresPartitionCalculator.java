package fractal.map.calc.request;

import org.apache.log4j.Logger;

import fractal.map.calc.CalcUtils;
import fractal.map.calc.Constants;
import fractal.map.message.request.AreaSquarePartitionRequest;
import fractal.map.message.response.AreaSquarePartitionResponse;
import fractal.map.message.response.SquareInfo;
import fractal.map.model.Layer;
import fractal.map.model.LayerRegistry;

public class SquaresPartitionCalculator
{
	private static final Logger logger = Logger.getLogger(SquaresPartitionCalculator.class);

	private final AreaSquarePartitionRequest request;

	private int canvasBasePointX;
	private int canvasBasePointY;
	private int areaHalfX;
	private int areaHalfY;

	public SquaresPartitionCalculator(AreaSquarePartitionRequest request)
	{
		this.request = request;
	};

	public AreaSquarePartitionResponse calculate()
	{
		Layer layer = LayerRegistry.getLayerByIndex(request.getLayerIndex());
		double pointWidth = layer.getPointWidth();

		areaHalfX = (int) Math.ceil(request.getAreaSizeX() / 2.0);
		areaHalfY = (int) Math.ceil(request.getAreaSizeY() / 2.0);

		double leftRe = CalcUtils.limitRe(request.getRe() - pointWidth * areaHalfX);
		double rightRe = CalcUtils.limitRe(request.getRe() + pointWidth * areaHalfX);
		double topIm = CalcUtils.limitIm(request.getIm() + pointWidth * areaHalfY);
		double bottomIm = CalcUtils.limitIm(request.getIm() - pointWidth * areaHalfY);

		int leftCount = (int) Math.floor((leftRe - Constants.RE_LEFT) / layer.getSquareWidth());
		int rightCount = (int) Math.floor((Constants.RE_RIGHT - rightRe) / layer.getSquareWidth());
		int topCount = (int) Math.floor((Constants.IM_TOP - topIm) / layer.getSquareWidth());
		int bottomCount =
		    (int) Math.floor((bottomIm - Constants.IM_BOTTOM) / layer.getSquareWidth());

		int leftSquareIndex = leftCount + 1;
		int rightSquareIndex = (int) layer.getSquaresBySide() - rightCount;
		int topSquareIndex = topCount + 1;
		int bottomSquareIndex = (int) layer.getSquaresBySide() - bottomCount;
		logger.debug("left: " + leftSquareIndex + ", right: " + rightSquareIndex + ", top: "
		        + topSquareIndex + ", bottom: " + bottomSquareIndex);

		AreaSquarePartitionResponse result =
		    new AreaSquarePartitionResponse(request.getRequestUUID());
		calcCanvasBasePointCoords(layer, leftSquareIndex, topSquareIndex);
		for (int i = topSquareIndex; i <= bottomSquareIndex; i++)
		{
			for (int j = leftSquareIndex; j <= rightSquareIndex; j++)
			{
				double squareRe = Constants.RE_LEFT + layer.getSquareWidth() * (j - 1);
				double squareIm = Constants.IM_TOP - layer.getSquareWidth() * (i - 1);
				int canvasLeftX =
				    canvasBasePointX + (j - leftSquareIndex) * Constants.SQUARE_SIDE_SIZE;
				int canvasTopY =
				    canvasBasePointY + (i - topSquareIndex) * Constants.SQUARE_SIDE_SIZE;
				SquareInfo squareInfo = new SquareInfo(squareRe, squareIm, canvasLeftX, canvasTopY);
				result.addSquareInfo(squareInfo);
			}
		}

		return result;
	}

	private void calcCanvasBasePointCoords(Layer layer, int leftSquareIndex, int topSquareIndex)
	{
		// Base point for squares partition should be shifted in viewport area.
		// If there is white space (few squares not cover area), then 
		// base point should be shifted into the area.
		// If squares partition is clipped by viewport, then
		// base point should be shifted out of area.
		double squareRe = Constants.RE_LEFT + layer.getSquareWidth() * (leftSquareIndex - 1);
		double squareIm = Constants.IM_TOP - layer.getSquareWidth() * (topSquareIndex - 1);
		int xDelta = (int) Math.ceil((request.getRe() - squareRe) / layer.getPointWidth());
		int yDelta = (int) Math.ceil((squareIm - request.getIm()) / layer.getPointWidth());
		canvasBasePointX = areaHalfX - xDelta;
		canvasBasePointY = areaHalfY - yDelta;
	}
}
