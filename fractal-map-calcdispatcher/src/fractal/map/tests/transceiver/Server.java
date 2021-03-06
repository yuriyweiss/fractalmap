package fractal.map.tests.transceiver;

import org.apache.log4j.PropertyConfigurator;

import fractal.map.conf.Configuration;
import fractal.map.monitor.MonitorLoggingMode;
import fractal.map.monitor.MonitorThread;
import fractal.map.tests.transceiver.message.LongMessageBufferPool;
import fractal.map.tests.transceiver.message.MessagesRegistrator;
import fractal.map.transceiver.server.TransceiverServer;
import fractal.map.util.ThreadUtils;

public class Server
{
	private static MonitorThread monitorThread = null;
	private static TransceiverServer transceiverServer = null;

	public static void main(String[] args) throws Exception
	{
		PropertyConfigurator.configure("conf/trans-server-log4j.conf");
		Configuration.load("conf/calcdisp.conf");
		LongMessageBufferPool.init(200000, 100);
		MessagesRegistrator.registerMessages();

		monitorThread = new MonitorThread(MonitorLoggingMode.TRANSCEIVER);

		transceiverServer =
		    new TransceiverServer(Configuration.getTransceiverReadPort(), Configuration.getTransceiverWritePort());
		transceiverServer.setMessageProcessor(new DummyServerMessageProcessor(transceiverServer));
		transceiverServer.start();

		monitorThread.start();

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				Server.stopApplication();
				ThreadUtils.sleep(1000);
			}
		});
	}

	public static void stopApplication()
	{
		if (monitorThread != null)
		{
			monitorThread.terminate();
		}
		if (transceiverServer != null)
		{
			transceiverServer.stop();
		}
	}
}
