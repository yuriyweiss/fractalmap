package fractal.map.transceiver;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import fractal.map.util.StoppableThread;

public abstract class CommunicationThread extends StoppableThread
{
	private static final Logger logger = Logger.getLogger(CommunicationThread.class);

	protected final Transceiver owner;

	private BlockingQueue<Transportable> messages = new LinkedBlockingQueue<Transportable>(10000);

	public CommunicationThread(Transceiver owner)
	{
		super();
		this.owner = owner;
	}

	protected UUID getClientUUID()
	{
		return null;
	}

	public void decodeMessage(ByteBuffer inputBuffer)
	{
		try
		{
			Transportable message = TransportableFactory.readFromByteBuffer(inputBuffer);
			owner.onMessageDecoded(getClientUUID(), message);
		}
		catch (Exception e)
		{
			logger.error("message processing failed");
			logger.debug("error stack: ", e);
		}
	}

	public void offerMessage(Transportable message)
	{
		if (!messages.offer(message))
		{
			logger.warn("message ignored, worker queue full");
			logger.debug(message.getIgnoredMessageInfo());
		}
	}

	protected BlockingQueue<Transportable> getMessages()
	{
		return messages;
	}
}
