package fractal.map.launcher;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractal.map.conf.Configuration;
import fractal.map.launcher.kpi.SquareQueueSizeKpi;
import fractal.map.message.MessagesRegistrator;
import fractal.map.message.ServletMessage;
import fractal.map.monitor.MonitorLoggingMode;
import fractal.map.monitor.MonitorThread;
import fractal.map.transceiver.server.ServerMessage;
import fractal.map.transceiver.server.TransceiverServer;
import fractal.map.util.ThreadUtils;

public class CalcDispatcher
{
	private static final Logger logger = Logger.getLogger(CalcDispatcher.class);

	private static MonitorThread monitorThread = null;
	private static TransceiverServer transceiverServer = null;
	private static ThreadPoolExecutor calcExecutor;
	private static ThreadPoolExecutor squareExecutor;

	public static void main(String[] args) throws Exception
	{
		PropertyConfigurator.configure("conf/calcdisp-log4j.conf");
		Configuration.load("conf/calcdisp.conf");

		MessagesRegistrator.registerMessages();

		monitorThread = new MonitorThread(MonitorLoggingMode.CALC | MonitorLoggingMode.TRANSCEIVER);

		calcExecutor = createProcessingExecutor("calcExecutor", 10, 20, 10000);
		squareExecutor = createProcessingExecutor("squareExecutor", 10, 100, 5000);
		monitorThread.registerAdditionalKpi(new SquareQueueSizeKpi(squareExecutor));

		transceiverServer =
		    new TransceiverServer(Configuration.getTransceiverReadPort(), Configuration.getTransceiverWritePort());
		transceiverServer.setMessageProcessor(new IncomingMessageProcessor(calcExecutor, squareExecutor, transceiverServer));
		transceiverServer.start();

		monitorThread.start();

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				CalcDispatcher.stopApplication();
			}
		});
	}

	private static ThreadPoolExecutor createProcessingExecutor(final String executorName,
	        int corePoolSize, int maxPoolSize, int queueCapacity)
	{
		ThreadPoolExecutor result =
		    new ThreadPoolExecutor(corePoolSize, maxPoolSize, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity));
		result.setRejectedExecutionHandler(new RejectedExecutionHandler()
		{
			@Override
			public void rejectedExecution(Runnable task, ThreadPoolExecutor executor)
			{
				ServerMessage request = ((MessageProcessingTask) task).getMessage();
				logMessageIgnored(executorName, (ServletMessage) request.getBody());
			}
		});
		return result;
	}

	private static void logMessageIgnored(String executorName, ServletMessage message)
	{
		logger.warn(executorName + " message ignored, executor queue full");
		logger.debug(message.getIgnoredMessageInfo());
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
			// wait 10 seconds
			ThreadUtils.sleep(10000L);
		}
		stopProcessingExecutor("calcExecutor", calcExecutor);
		stopProcessingExecutor("squareExecutor", squareExecutor);
	}

	private static void stopProcessingExecutor(String executorName, ThreadPoolExecutor executor)
	{
		executor.shutdown();
		try
		{
			// Wait a while for existing tasks to terminate
			if (!executor.awaitTermination(30, TimeUnit.SECONDS))
			{
				logger.info(executorName + " try force shutdown");
				executor.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!executor.awaitTermination(30, TimeUnit.SECONDS))
				{
					logger.error(executorName + " hadn't terminated at appropriate time");
				}
			}
			logger.info(executorName + " shutdown success");
		}
		catch (InterruptedException ie)
		{
			// (Re-)Cancel if current thread also interrupted
			logger.info(executorName + " try force shutdown on interruption");
			executor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
		logger.info(executorName + " shutdown process finished");
	}
}
