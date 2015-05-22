package fractal.map.servlet.app;

import java.util.UUID;

import fractal.map.message.ServletMessage;
import fractal.map.waiterscache.ResponseWaiter;
import fractal.map.waiterscache.WaitersCache;

public class RequestsCache
{
	private static final WaitersCache waiters = new WaitersCache(15);

	public static void registerWaiter(UUID requestUUID, ResponseWaiter waiter)
	{
		waiters.put(requestUUID, waiter);
	}

	public static void onResponseArrived(UUID requestUUID, ServletMessage response)
	{
		waiters.onResponseArrived(requestUUID, response);
	}
}
