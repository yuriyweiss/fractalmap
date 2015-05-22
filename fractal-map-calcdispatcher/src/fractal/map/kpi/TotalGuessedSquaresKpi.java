package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class TotalGuessedSquaresKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		setValue(Context.getBaseCounters().getTotalGuessedSquares().get());
	}
}
