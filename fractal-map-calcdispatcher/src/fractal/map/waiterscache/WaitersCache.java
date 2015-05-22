package fractal.map.waiterscache;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import fractal.map.message.ServletMessage;
import fractal.map.util.StoppableThread;

public class WaitersCache
{
	private static final Logger logger = Logger.getLogger(WaitersCache.class);

	private Cache<UUID, ResponseWaiter> waiters;
	private StoppableThread cleanUpThread;

	public WaitersCache(int waiterTimeoutSeconds)
	{
		String cacheSpec =
		    "initialCapacity=1000,maximumSize=10000,expireAfterWrite=" + waiterTimeoutSeconds + "s";

		waiters = CacheBuilder.from(cacheSpec).removalListener(new WaiterRemovalListener()).build();
		createCleanUpThread();
		cleanUpThread.start();
	}

	private void createCleanUpThread()
	{
		cleanUpThread = new StoppableThread()
		{
			@Override
			public void run()
			{
				while (canRun())
				{
					try
					{
						Thread.sleep(10000L);
						waiters.cleanUp();
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		};
	}

	public void onResponseArrived(UUID requestUUID, ServletMessage response)
	{
		ResponseWaiter waiter = waiters.getIfPresent(requestUUID);
		if (waiter != null)
		{
			waiters.invalidate(requestUUID);
			synchronized (waiter)
			{
				waiter.setResponse(response);
				waiter.notifyAll();
			}
		}
		else
		{
			logger.error("waiter not found for requestUUID: " + requestUUID);
		}
	}

	public void put(UUID requestUUID, ResponseWaiter waiter)
	{
		waiters.put(requestUUID, waiter);
	}

	public void stop()
	{
		cleanUpThread.terminate();
	}

	private class WaiterRemovalListener implements RemovalListener<UUID, ResponseWaiter>
	{
		@Override
		public void onRemoval(RemovalNotification<UUID, ResponseWaiter> notification)
		{
			if (notification.getCause() == RemovalCause.EXPIRED)
			{
				logger.warn("request TIMEOUT: " + notification.getKey());
				ResponseWaiter waiter = notification.getValue();
				synchronized (waiter)
				{
					waiter.setResponseTimeout();
					waiter.notifyAll();
				}
			}
		}
	}

	public long getSize()
	{
		return waiters.size();
	}
}
