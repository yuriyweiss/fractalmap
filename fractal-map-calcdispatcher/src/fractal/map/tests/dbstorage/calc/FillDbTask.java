package fractal.map.tests.dbstorage.calc;

import java.sql.Connection;

import org.apache.log4j.Logger;

import fractal.map.calc.square.SquaresPartition;
import fractal.map.conf.Configuration;
import fractal.map.context.Context;
import fractal.map.model.Layer;
import fractal.map.storage.oracle.SaveSquareToOracleStrategy;

public class FillDbTask implements Runnable
{
	private static final Logger logger = Logger.getLogger(FillDbTask.class);

	private final Connection conn;

	public FillDbTask(Connection conn)
	{
		this.conn = conn;
	}

	@Override
	public void run()
	{
		try
		{
			logger.info("DB calculation task started");
			Layer layer = prepareLayer();
			createSquaresPartition(layer);
			calculateSquares();
			logger.info("DB calculation task finished");
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

	private void createSquaresPartition(Layer layer)
	{
		SquaresPartition squaresPartition = new SquaresPartition(layer);
		Context.setSquaresPartition(squaresPartition);
	}

	private void calculateSquares() throws Exception
	{
		Context.getSquaresPartition().calculateAndSaveSquares(new SaveSquareToOracleStrategy(conn));
	}
}
