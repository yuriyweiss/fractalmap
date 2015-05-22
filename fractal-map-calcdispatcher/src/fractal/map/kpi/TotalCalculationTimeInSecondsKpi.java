package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractDoubleKpi;

public class TotalCalculationTimeInSecondsKpi extends AbstractDoubleKpi
{
	@Override
	public void updateValue()
	{
		setValue(Context.getBaseCounters().getTotalCalculationTimeMillis().get() / 1000.0);
	}
}
