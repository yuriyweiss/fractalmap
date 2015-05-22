package fractal.map.tests.load;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractal.map.conf.Configuration;
import fractal.map.monitor.MonitorThread;
import fractal.map.tests.load.calc.CalculationTask;

public class LoadGenerator
{
	private static final Logger logger = Logger.getLogger(LoadGenerator.class);

	public static void main(String[] args) throws Exception
	{
		// Launch MySQL before running.
		PropertyConfigurator.configure("conf/load-test-log4j.conf");
		Configuration.load("conf/load-test.conf");

		MonitorThread monitorThread = new MonitorThread();

		ExecutorService executor = Executors.newFixedThreadPool(1);
		try
		{
			//Future<?> future = executor.submit(new CalculationTask("result/result_sample"));
			Future<?> future = executor.submit(new CalculationTask("result/result_opt_square"));
			monitorThread.start();
			try
			{
				future.get();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			monitorThread.terminate();
			executor.shutdown();
		}

		logger.info("application stopped");
	}
}
