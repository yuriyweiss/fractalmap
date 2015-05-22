package fractal.map.tests.load.compare;

import static fractal.map.calc.Constants.SQUARE_SIDE_SIZE;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gson.Gson;

import fractal.map.calc.Constants;
import fractal.map.calc.LoadSquareStrategy;
import fractal.map.calc.square.SquaresPartition;
import fractal.map.conf.Configuration;
import fractal.map.model.Layer;
import fractal.map.model.Square;
import fractal.map.storage.combined.CombinedLoadSquareStrategy;
import fractal.map.tests.load.LoadGenerator;

public class SquaresComparator
{
	private static final Logger logger = Logger.getLogger(LoadGenerator.class);

	private static SquaresPartition squaresPartition;
	private static CombinedLoadSquareStrategy optimizedCalcLoadStrategy;
	private static CombinedLoadSquareStrategy straightCalcLoadStrategy;

	public static void main(String[] args)
	{
		// Launch MySQL before running.

		// Run LoadGenerator in two modes for two different
		// storage destinations: result/result_sample and result/result_opt_square.
		PropertyConfigurator.configure("conf/load-test-log4j.conf");
		try
		{
			Configuration.load("conf/load-test.conf");
			Layer layer = prepareLayer();
			squaresPartition = new SquaresPartition(layer);
			optimizedCalcLoadStrategy = new CombinedLoadSquareStrategy("result/result_opt_square");
			straightCalcLoadStrategy = new CombinedLoadSquareStrategy("result/result_sample");
			compareSquares();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static Layer prepareLayer()
	{
		int layerIndex = Configuration.getLayerIndex();
		int iterations = Configuration.getIterationsCount();
		long layerSideSize = Configuration.getLayerSideSize();
		return new Layer(layerIndex, iterations, layerSideSize);
	}

	private static void compareSquares() throws Exception
	{
		for (int y = 0; y < squaresPartition.getSquaresByY(); y++)
		{
			for (int x = 0; x < squaresPartition.getSquaresByX(); x++)
			{
				compareSquareResults(squaresPartition.getSquare(y, x));
			}
		}
	}

	private static void compareSquareResults(Square square) throws Exception
	{
		Square loadedSquare =
		    straightCalcLoadStrategy.loadSquare(square.getLayer().getLayerIndex(),
		            square.getLeftRe(), square.getTopIm());
		if (loadedSquare.getIterations() != Constants.ITERATIONS_DIFFER)
		{
			System.out.println("ITERATIONS [" + loadedSquare.getIterations() + "] common for "
			        + loadedSquare);
			return;
		}

		int[][] pointsSample = loadSquareSample(loadedSquare);
		int[][] pointsOptimized = loadSquareOptimized(loadedSquare);
		int equalPoints = 0;
		int diffPoints = 0;
		for (int y = 0; y < SQUARE_SIDE_SIZE; y++)
		{
			for (int x = 0; x < SQUARE_SIDE_SIZE; x++)
			{
				if (pointsSample[y][x] == pointsOptimized[y][x])
				{
					equalPoints++;
				}
				else
				{
					diffPoints++;
				}
			}
		}
		String message = (diffPoints == 0) ? "PASS " : "FAIL ";
		message += String.format("y%3d_x%3d", square.getLayerY(), square.getLayerX());
		message += "\t\tequal/diff: " + equalPoints + "/" + diffPoints;
		logger.info(message);
	}

	private static int[][] loadSquareOptimized(Square square) throws Exception
	{
		return loadSquare(square, optimizedCalcLoadStrategy);
	}

	private static int[][] loadSquare(Square square, LoadSquareStrategy loadStrategy)
	        throws Exception
	{
		int[][] result;

		byte[] squareBody = loadStrategy.loadSquareBody(square);
		InputStreamReader isr =
		    new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(squareBody)), StandardCharsets.UTF_8);
		try
		{
			result = new Gson().fromJson(isr, int[][].class);
		}
		finally
		{
			isr.close();
		}
		return result;
	}

	private static int[][] loadSquareSample(Square square) throws Exception
	{
		return loadSquare(square, straightCalcLoadStrategy);
	}
}
