package fractal.map.tests.launcher;

import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractal.map.conf.Configuration;
import fractal.map.message.MessagesRegistrator;
import fractal.map.message.request.PointCoordsRequest;
import fractal.map.monitor.MonitorLoggingMode;
import fractal.map.monitor.MonitorThread;
import fractal.map.tests.launcher.kpi.WaitersCacheSizeKpi;
import fractal.map.transceiver.client.TransceiverClient;
import fractal.map.util.ThreadUtils;
import fractal.map.waiterscache.WaitersCache;

public class CalcDispatcherTesterClient
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CalcDispatcherTesterClient.class);

	private static MonitorThread monitorThread = null;

	public static void main(String[] args) throws Exception
	{
		PropertyConfigurator.configure("conf/calcdisp-tester-log4j.conf");
		Configuration.load("conf/calcdisp.conf");
		MessagesRegistrator.registerMessages();

		monitorThread = new MonitorThread(MonitorLoggingMode.TRANSCEIVER);

		WaitersCache waiters = new WaitersCache(60);
		monitorThread.registerAdditionalKpi(new WaitersCacheSizeKpi(waiters));

		String ipAddress = "127.0.0.1";
		// Flip read/write ports to connect to server.
		TransceiverClient transceiverClient =
		    new TransceiverClient(Configuration.getTransceiverWritePort(), Configuration.getTransceiverReadPort(), ipAddress);
		transceiverClient.setMessageProcessor(new ResponseMessageProcessor(transceiverClient, waiters));
		transceiverClient.start();

		monitorThread.start();

		Random random = new Random();
		while (true)
		{
			double re = -1.5 + 2 * random.nextDouble();
			double im = 1 - 2 * random.nextDouble();
			int shiftX = -250 + random.nextInt(500);
			int shiftY = -200 + random.nextInt(400);
			PointCoordsRequest request =
			    new PointCoordsRequest(UUID.randomUUID(), 5, re, im, shiftX, shiftY);
			waiters.put(request.getRequestUUID(), new DummyWaiter());
			transceiverClient.send(request);
			// long sleepTime = random.nextInt(10) + 10;
			long sleepTime = 1000L;
			ThreadUtils.sleep(sleepTime);
		}
	}
}
