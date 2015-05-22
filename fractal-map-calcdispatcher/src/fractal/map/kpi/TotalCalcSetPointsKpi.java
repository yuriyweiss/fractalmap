package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class TotalCalcSetPointsKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		setValue(Context.getBaseCounters().getTotalCalculatedSetPoints().get());
	}
}
