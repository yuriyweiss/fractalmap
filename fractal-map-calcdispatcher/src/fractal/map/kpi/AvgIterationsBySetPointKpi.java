package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class AvgIterationsBySetPointKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		long totalCalculatedSetPoints =
		    Context.getBaseCounters().getTotalCalculatedSetPoints().get();
		long totalIterationsBySetPoints =
		    Context.getBaseCounters().getTotalIterationsBySetPoints().get();
		if (totalCalculatedSetPoints == 0)
		{
			setValue(-1);
		}
		else
		{
			setValue(totalIterationsBySetPoints / totalCalculatedSetPoints);
		}
	}
}
