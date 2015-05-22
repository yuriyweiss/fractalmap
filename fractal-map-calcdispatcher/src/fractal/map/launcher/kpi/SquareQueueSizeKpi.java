package fractal.map.launcher.kpi;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import fractal.map.monitor.AbstractLongKpi;
import fractal.map.monitor.AdditionalKpi;

public class SquareQueueSizeKpi extends AbstractLongKpi implements AdditionalKpi
{
	private final ThreadPoolExecutor squareExecutor;

	public SquareQueueSizeKpi(ThreadPoolExecutor squareExecutor)
	{
		this.squareExecutor = squareExecutor;
	}

	@Override
	public void updateValue()
	{
		if (squareExecutor != null)
		{
			setValue(squareExecutor.getQueue().size());
		}
	}

	@Override
	public void logValue(Logger logger)
	{
		logger.info("QUEUE SIZE squareExecutors: " + getValue());
	}
}
