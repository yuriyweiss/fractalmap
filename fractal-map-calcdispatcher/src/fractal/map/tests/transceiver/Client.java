package fractal.map.tests.transceiver;

import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractal.map.conf.Configuration;
import fractal.map.monitor.MonitorLoggingMode;
import fractal.map.monitor.MonitorThread;
import fractal.map.tests.transceiver.message.LongDummyMessage;
import fractal.map.tests.transceiver.message.LongMessageBufferPool;
import fractal.map.tests.transceiver.message.MessagesRegistrator;
import fractal.map.tests.transceiver.message.ShortDummyMessage;
import fractal.map.transceiver.Transportable;
import fractal.map.transceiver.client.TransceiverClient;
import fractal.map.util.ThreadUtils;
import fractal.map.util.buffer.Buffer;

public class Client
{
	private static final Logger logger = Logger.getLogger(Client.class);

	private static MonitorThread monitorThread = null;
	private static long messageIndex = 1;
	@SuppressWarnings("unused")
	private static Random bufferSizeRandom = new Random();

	public static void main(String[] args) throws Exception
	{
		PropertyConfigurator.configure("conf/trans-client-log4j.conf");
		Configuration.load("conf/calcdisp.conf");
		LongMessageBufferPool.init(200000, 100);
		MessagesRegistrator.registerMessages();

		monitorThread = new MonitorThread(MonitorLoggingMode.TRANSCEIVER);
		monitorThread.start();

		String ipAddress = "127.0.0.1";
		// Flip read/write ports to connect to server.
		TransceiverClient transceiverClient =
		    new TransceiverClient(Configuration.getTransceiverWritePort(), Configuration.getTransceiverReadPort(), ipAddress);
		transceiverClient.setMessageProcessor(new DummyClientMessageProcessor());
		transceiverClient.start();

		@SuppressWarnings("unused")
		Random random = new Random();
		while (true)
		{
			for (int i = 0; i < 10; i++)
			{
				Transportable longMessage = buildLongMessage();
				if (longMessage != null)
				{
					transceiverClient.send(longMessage);
				}
				messageIndex++;
			}
			for (int i = 0; i < 50; i++)
			{
				transceiverClient.send(buildShortMessage());
				messageIndex++;
			}
			// long sleepTime = random.nextInt(10) + 10;
			long sleepTime = 10;
			ThreadUtils.sleep(sleepTime);
		}
	}

	private static Transportable buildLongMessage()
	{
		Buffer buffer = prepareLongMessageBuffer();
		if (buffer != null)
		{
			return new LongDummyMessage(messageIndex, buffer);
		}
		else
		{
			return null;
		}
	}

	private static Buffer prepareLongMessageBuffer()
	{
		Buffer result = null;
		try
		{
			result = LongMessageBufferPool.borrowObject();
			logger.trace("buffer borrowed for message: " + messageIndex);
		}
		catch (Exception e)
		{
			logger.error("buffer borrow failed");
			logger.debug("error stack: ", e);
		}
		if (result == null) return null;

		// int arrayLength = bufferSizeRandom.nextInt(150000);
		int arrayLength = 100000;
		result.setUsedCount(arrayLength);
		byte[] bytes = result.getBytes();
		for (int i = 0; i < arrayLength; i++)
		{
			bytes[i] = 25;
		}
		return result;
	}

	private static Transportable buildShortMessage()
	{
		return new ShortDummyMessage(messageIndex, "1111111111111");
	}
}
