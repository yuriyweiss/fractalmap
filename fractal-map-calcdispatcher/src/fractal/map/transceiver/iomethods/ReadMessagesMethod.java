package fractal.map.transceiver.iomethods;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import fractal.map.transceiver.CommunicationThread;

public class ReadMessagesMethod
{
	private static final Logger logger = Logger.getLogger(ReadMessagesMethod.class);

	private final CommunicationThread caller;
	private final ByteBuffer inputBuffer;

	public ReadMessagesMethod(CommunicationThread caller, ByteBuffer inputBuffer)
	{
		this.caller = caller;
		this.inputBuffer = inputBuffer;
	}

	public void execute()
	{
		int position = 0;
		int len = 0;
		int messagesProcessed = 0;
		try
		{
			logger.trace("received byteBuffer size: " + inputBuffer.position());
			// While something is in the buffer. (write mode - check position)
			while (inputBuffer.position() > 4)
			{
				// Switch writing -> reading.
				inputBuffer.flip();
				// Get next message length, but restore position to append data if incomplete.
				inputBuffer.mark();
				len = inputBuffer.getInt();
				inputBuffer.reset();
				// Exit if package is incomplete. (read mode - check limit)
				if (inputBuffer.limit() < len + 4)
				{
					logger.trace("buffer incomplete");
					logger.trace("waiting: " + len + " bytes, current: " + inputBuffer.limit()
					        + " bytes");
					inputBuffer.compact();
					break;
				}

				logger.trace("buffer size: " + inputBuffer.limit());
				logger.trace("message length: " + len);
				// Read but ignore 4 bytes of message length.
				inputBuffer.getInt();

				caller.decodeMessage(inputBuffer);
				messagesProcessed++;

				// Switch reading -> writing.
				if (inputBuffer.hasRemaining())
				{
					inputBuffer.compact();
				}
				else
				{
					inputBuffer.clear();
				}
			}
		}
		catch (RuntimeException e)
		{
			logger.trace("position: " + position + "; len: " + Integer.toBinaryString(len)
			        + "; messagesProcessed: " + messagesProcessed);
			throw e;
		}
	}
}
