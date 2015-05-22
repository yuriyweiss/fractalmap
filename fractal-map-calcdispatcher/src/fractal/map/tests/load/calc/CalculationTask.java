package fractal.map.tests.load.calc;

import org.apache.log4j.Logger;

import fractal.map.calc.square.SquaresPartition;
import fractal.map.conf.Configuration;
import fractal.map.context.Context;
import fractal.map.model.Layer;
import fractal.map.storage.combined.CombinedSaveSquareStrategy;

public class CalculationTask implements Runnable
{
	private static final Logger logger = Logger.getLogger(CalculationTask.class);

	private final String rootDir;

	private Layer layer;

	public CalculationTask(String rootDir)
	{
		this.rootDir = rootDir;
	}

	@Override
	public void run()
	{
		try
		{
			logger.info("FILE calculation task started");
			layer = prepareLayer();
			Context.getBaseCounters().setSquaresToCalculate(
			        layer.getSquaresBySide() * layer.getSquaresBySide() / 2);
			createSquaresPartition();
			calculateSquares();
			logger.info("FILE calculation task finished");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Layer prepareLayer()
	{
		int layerIndex = Configuration.getLayerIndex();
		int iterations = Configuration.getIterationsCount();
		long layerSideSize = Configuration.getLayerSideSize();
		return new Layer(layerIndex, iterations, layerSideSize);
	}

	private void createSquaresPartition()
	{
		SquaresPartition squaresPartition = new SquaresPartition(layer);
		Context.setSquaresPartition(squaresPartition);
	}

	private void calculateSquares() throws Exception
	{
		Context.getSquaresPartition().calculateAndSaveSquares(
		        new CombinedSaveSquareStrategy(rootDir));
	}
}
