package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class SquaresPerFiveSecondsKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		setValue(Context.getBaseCounters().getPeriodicProcessedSquares().get());
		Context.getBaseCounters().getPeriodicProcessedSquares().set(0L);
	}
}
