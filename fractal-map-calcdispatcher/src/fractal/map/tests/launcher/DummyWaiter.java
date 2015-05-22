package fractal.map.tests.launcher;

import org.apache.log4j.Logger;

import fractal.map.message.ServletMessage;
import fractal.map.waiterscache.ResponseWaiter;

public class DummyWaiter implements ResponseWaiter
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DummyWaiter.class);

	@Override
	public void setResponse(ServletMessage response)
	{
		//logger.debug("request processed: " + response.getRequestUUID());
	}

	@Override
	public void setResponseTimeout()
	{
		// TODO empty
	}
}
