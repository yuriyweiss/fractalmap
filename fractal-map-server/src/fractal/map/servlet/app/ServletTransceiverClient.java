package fractal.map.servlet.app;

import javax.servlet.ServletContext;

import fractal.map.transceiver.Transportable;
import fractal.map.transceiver.client.TransceiverClient;
import fractal.map.util.ThreadUtils;

public class ServletTransceiverClient
{
	private static TransceiverClient transceiverClient = null;

	public static void startTransceiver(ServletContext context)
	{
		String calcdispIpAddress = context.getInitParameter("calcdisp.ip.address");
		int readPort = Integer.parseInt(context.getInitParameter("calcdisp.read.port"));
		int writePort = Integer.parseInt(context.getInitParameter("calcdisp.write.port"));
		transceiverClient = new TransceiverClient(readPort, writePort, calcdispIpAddress);
		transceiverClient.setMessageProcessor(new ResponseMessageProcessor());
		transceiverClient.start();
	}

	public static void stopTransceiver()
	{
		if (transceiverClient != null)
		{
			transceiverClient.stop();
			transceiverClient = null;
			ThreadUtils.sleep(5000L);
		}
	}

	public static void send(Transportable message)
	{
		if (transceiverClient != null)
		{
			transceiverClient.send(message);
		}
	}
}
