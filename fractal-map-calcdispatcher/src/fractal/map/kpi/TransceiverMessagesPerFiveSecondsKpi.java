package fractal.map.kpi;

import fractal.map.context.Context;
import fractal.map.monitor.AbstractLongKpi;

public class TransceiverMessagesPerFiveSecondsKpi extends AbstractLongKpi
{
	@Override
	public void updateValue()
	{
		setValue(Context.getBaseCounters().getPeriodicTransceiverMessages().get());
		Context.getBaseCounters().getPeriodicTransceiverMessages().set(0L);
	}
}
