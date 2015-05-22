package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractDoubleKpi;

public class AvgSquareCalcTimeKpi extends AbstractDoubleKpi
{
	@Override
	public void updateValue()
	{
		long totalProcessedSquares = Context.getBaseCounters().getTotalProcessedSquares().get();
		long totalGuessedSquares = Context.getBaseCounters().getTotalGuessedSquares().get();
		long totalCalculationTimeMillis =
		    Context.getBaseCounters().getTotalCalculationTimeMillis().get();
		if (totalProcessedSquares == 0)
		{
			setValue(-1);
		}
		else
		{
			setValue(totalCalculationTimeMillis
			        / (double) (totalProcessedSquares - totalGuessedSquares));
		}
	}
}
