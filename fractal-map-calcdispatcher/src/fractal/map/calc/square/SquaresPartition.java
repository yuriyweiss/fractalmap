package fractal.map.calc.square;

import static fractal.map.calc.Constants.SQUARE_SIDE_SIZE;

import org.apache.log4j.Logger;

import fractal.map.calc.CalcUtils;
import fractal.map.calc.Constants;
import fractal.map.calc.SaveSquareStrategy;
import fractal.map.model.Layer;
import fractal.map.model.Square;

public class SquaresPartition
{
	private static final Logger logger = Logger.getLogger(SquaresPartition.class);

	private final Layer layer;
	private Square[][] partition;
	private int squaresByX;
	private int squaresByY;

	public SquaresPartition(Layer layer)
	{
		this.layer = layer;
		squaresByX = (int) layer.getSquaresBySide();
		squaresByY = squaresByX / 2;
		partition = new Square[squaresByY][squaresByX];
		initSquares();
	}

	private void initSquares()
	{
		double squareWidth = layer.getSquareWidth();
		for (int layerY = 0; layerY < squaresByY; layerY++)
		{
			for (int layerX = 0; layerX < squaresByX; layerX++)
			{
				partition[layerY][layerX] =
				    new Square(layer, Constants.RE_LEFT + layerX * squareWidth, Constants.IM_TOP
				            - layerY * squareWidth);
			}
		}
		logger.info("partition squares initialized");
	}

	public void calculateAndSaveSquares(SaveSquareStrategy saveStrategy) throws Exception
	{
		logger.info("squares calculation started");
		for (int layerY = 0; layerY < squaresByY; layerY++)
		{
			for (int layerX = 0; layerX < squaresByX; layerX++)
			{
				Square square = partition[layerY][layerX];
				int[][] points = new int[SQUARE_SIDE_SIZE][SQUARE_SIDE_SIZE];
				CalcUtils.initPointsNeedCalculation(points);
				square.calculatePoints(points);
				int iterations = CalcUtils.getCommonIteration(points);
				square.setIterations(iterations);
				saveStrategy.save(square, points);
			}
		}
		logger.info("squares calculation finished");
	}

	public int getSquaresByX()
	{
		return squaresByX;
	}

	public int getSquaresByY()
	{
		return squaresByY;
	}

	public Square getSquare(int layerY, int layerX)
	{
		return partition[layerY][layerX];
	}
}
