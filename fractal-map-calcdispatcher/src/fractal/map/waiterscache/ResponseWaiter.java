package fractal.map.waiterscache;

import fractal.map.message.ServletMessage;

public interface ResponseWaiter
{
	public void setResponse(ServletMessage response);

	public void setResponseTimeout();
}
