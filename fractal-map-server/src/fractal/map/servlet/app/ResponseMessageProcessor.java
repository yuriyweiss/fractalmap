package fractal.map.servlet.app;

import java.util.UUID;

import fractal.map.message.ServletMessage;
import fractal.map.transceiver.MessageProcessor;
import fractal.map.transceiver.Transportable;

public class ResponseMessageProcessor implements MessageProcessor
{
	@Override
	public void onMessageDecoded(UUID requestUUID, Transportable message)
	{
		ServletMessage response = (ServletMessage) message;
		RequestsCache.onResponseArrived(response.getRequestUUID(), response);
	}
}
