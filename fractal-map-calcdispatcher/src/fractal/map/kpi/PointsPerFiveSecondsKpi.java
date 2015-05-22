package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class PointsPerFiveSecondsKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		setValue(Context.getBaseCounters().getPeriodicCalculatedPoints().get());
		Context.getBaseCounters().getPeriodicCalculatedPoints().set(0L);
	}
}
