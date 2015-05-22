package fractal.map.transceiver.iomethods;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import fractal.map.conf.Configuration;
import fractal.map.transceiver.Transportable;

public class WriteMessagesMethod
{
	private static final Logger logger = Logger.getLogger(WriteMessagesMethod.class);

	private final BlockingQueue<Transportable> messages;
	private final SocketChannel socketChannel;
	private final ByteBuffer outputBuffer;
	private final int bufferCapacity;

	public WriteMessagesMethod(BlockingQueue<Transportable> messages, SocketChannel socketChannel,
	    ByteBuffer outputBuffer)
	{
		this.messages = messages;
		this.socketChannel = socketChannel;
		this.outputBuffer = outputBuffer;
		this.bufferCapacity = Configuration.getTransceiverBufferSize();
	}

	public void execute() throws IOException
	{
		List<Transportable> messagesToSend = new ArrayList<Transportable>();
		messages.drainTo(messagesToSend);

		int bufferPosition = -1;
		int messageLength = -1;
		try
		{
			for (Transportable message : messagesToSend)
			{
				int len = message.estimateLength();
				messageLength = len;
				bufferPosition = outputBuffer.position();
				// Write buffer to channel if capacity will be exceeded.
				if ((outputBuffer.position() + len + 4) > (bufferCapacity - 1))
				{
					writeBufferToChannel(outputBuffer);
				}
				message.writeToByteBuffer(outputBuffer);
				message.releaseResources();
			}
			// Write remaining messages to channel.
			if (outputBuffer.position() > 0)
			{
				writeBufferToChannel(outputBuffer);
			}
		}
		catch (BufferOverflowException e)
		{
			logger.info("messageLength: " + messageLength + ", bufferPosition: " + bufferPosition);
			throw e;
		}
	}

	private void writeBufferToChannel(ByteBuffer outputBuffer) throws IOException
	{
		outputBuffer.flip();
		while (outputBuffer.hasRemaining())
		{
			socketChannel.write(outputBuffer);
		}
		outputBuffer.clear();
	}
}
