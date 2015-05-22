package fractal.map.launcher;

import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import fractal.map.context.Context;
import fractal.map.message.request.Request;
import fractal.map.transceiver.MessageProcessor;
import fractal.map.transceiver.Transportable;
import fractal.map.transceiver.server.ServerMessage;
import fractal.map.transceiver.server.TransceiverServer;

public class IncomingMessageProcessor implements MessageProcessor
{
	private final ThreadPoolExecutor calcExecutor;
	private final ThreadPoolExecutor squareExecutor;
	private final TransceiverServer transceiverServer;

	public IncomingMessageProcessor(ThreadPoolExecutor calcExecutor,
	    ThreadPoolExecutor squareExecutor, TransceiverServer transceiverServer)
	{
		this.calcExecutor = calcExecutor;
		this.squareExecutor = squareExecutor;
		this.transceiverServer = transceiverServer;
	}

	@Override
	public void onMessageDecoded(UUID clientUUID, Transportable message)
	{
		Context.getBaseCounters().getPeriodicTransceiverMessages().getAndIncrement();
		Context.getBaseCounters().getTotalTransceiverMessages().getAndIncrement();

		ServerMessage request = new ServerMessage(clientUUID, message);
		if (!(message instanceof Request)) return;

		MessageProcessingTask messageProcessingTask =
		    new MessageProcessingTask(request, transceiverServer);
		if (((Request) message).isCalcRequest())
		{
			calcExecutor.execute(messageProcessingTask);
		}
		else
		{
			squareExecutor.execute(messageProcessingTask);
		}
	}
}
