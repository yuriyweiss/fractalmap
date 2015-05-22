package fractal.map.tests.transceiver;

import java.util.UUID;

import fractal.map.context.Context;
import fractal.map.transceiver.MessageProcessor;
import fractal.map.transceiver.Transportable;

public class DummyClientMessageProcessor implements MessageProcessor
{
	@Override
	public void onMessageDecoded(UUID clientUUID, Transportable message)
	{
		Context.getBaseCounters().getPeriodicTransceiverMessages().getAndIncrement();
		Context.getBaseCounters().getTotalTransceiverMessages().getAndIncrement();
	}
}
