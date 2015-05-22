package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class AvgCalcPointsInSquareKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		long totalCalculatedPoints = Context.getBaseCounters().getTotalCalculatedPoints().get();
		long totalProcessedSquares = Context.getBaseCounters().getTotalProcessedSquares().get();
		long totalGuessedSquares = Context.getBaseCounters().getTotalGuessedSquares().get();
		if (totalProcessedSquares == 0)
		{
			setValue(-1);
		}
		else
		{
			setValue(totalCalculatedPoints / (totalProcessedSquares - totalGuessedSquares));
		}
	}
}
