package fractal.map.tests.transceiver;

import java.util.UUID;

import fractal.map.context.Context;
import fractal.map.tests.transceiver.message.LongDummyMessage;
import fractal.map.tests.transceiver.message.MessagesRegistrator;
import fractal.map.tests.transceiver.message.ShortDummyMessage;
import fractal.map.transceiver.MessageProcessor;
import fractal.map.transceiver.Transportable;
import fractal.map.transceiver.server.ServerMessage;
import fractal.map.transceiver.server.TransceiverServer;

public class DummyServerMessageProcessor implements MessageProcessor
{
	private final TransceiverServer owner;

	public DummyServerMessageProcessor(TransceiverServer owner)
	{
		this.owner = owner;
	}

	@Override
	public void onMessageDecoded(UUID clientUUID, Transportable message)
	{
		Context.getBaseCounters().getPeriodicTransceiverMessages().getAndIncrement();
		Context.getBaseCounters().getTotalTransceiverMessages().getAndIncrement();

		ShortDummyMessage reply = null;
		if (message.getClassId() == MessagesRegistrator.SHORT_DUMMY)
		{
			ShortDummyMessage request = (ShortDummyMessage) message;
			reply = new ShortDummyMessage(request.getMessageIndex(), request.getMessageString());
		}
		else if (message.getClassId() == MessagesRegistrator.LONG_DUMMY)
		{
			LongDummyMessage request = (LongDummyMessage) message;
			reply =
			    new ShortDummyMessage(request.getMessageIndex(), Integer.toString(request.getMessageLength()));
			request.releaseResources();
		}
		if (reply != null)
		{
			owner.send(new ServerMessage(clientUUID, reply));
		}
	}
}
