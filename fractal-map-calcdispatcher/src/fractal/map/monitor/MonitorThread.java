package fractal.map.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fractal.map.kpi.AvgCalcPointsInSquareKpi;
import fractal.map.kpi.AvgIterationsBySetPointKpi;
import fractal.map.kpi.AvgPointCalcTimeKpi;
import fractal.map.kpi.AvgSquareCalcTimeKpi;
import fractal.map.kpi.PointsPerFiveSecondsKpi;
import fractal.map.kpi.SquaresPerFiveSecondsKpi;
import fractal.map.kpi.TotalCalcSetPointsKpi;
import fractal.map.kpi.TotalCalculationTimeInSecondsKpi;
import fractal.map.kpi.TotalGuessedSquaresKpi;
import fractal.map.kpi.TotalProcessedSquaresKpi;
import fractal.map.kpi.TotalSquaresToCalculateKpi;
import fractal.map.kpi.TotalTransceiverMessagesKpi;
import fractal.map.kpi.TransceiverMessagesPerFiveSecondsKpi;
import fractal.map.util.StoppableThread;

public class MonitorThread extends StoppableThread
{
	private static final Logger logger = Logger.getLogger(MonitorThread.class);

	private List<Kpi> kpis = new ArrayList<Kpi>();
	private List<AdditionalKpi> additionalKpis = new ArrayList<AdditionalKpi>();

	private int loggingMode = MonitorLoggingMode.CALC | MonitorLoggingMode.TRANSCEIVER;

	private final AvgSquareCalcTimeKpi avgSquareCalcTimeKpi = new AvgSquareCalcTimeKpi();
	private final AvgPointCalcTimeKpi avgPointCalcTimeKpi = new AvgPointCalcTimeKpi();
	private final AvgCalcPointsInSquareKpi avgCalcPointsInSquareKpi =
	    new AvgCalcPointsInSquareKpi();
	private final AvgIterationsBySetPointKpi avgIterationsBySetPointKpi =
	    new AvgIterationsBySetPointKpi();
	private final TotalCalculationTimeInSecondsKpi totalCalculationTimeInSecondsKpi =
	    new TotalCalculationTimeInSecondsKpi();
	private final TotalSquaresToCalculateKpi totalSquaresToCalculateKpi =
	    new TotalSquaresToCalculateKpi();
	private final TotalProcessedSquaresKpi totalProcessedSquaresKpi =
	    new TotalProcessedSquaresKpi();
	private final TotalGuessedSquaresKpi totalGuessedSquaresKpi = new TotalGuessedSquaresKpi();
	private final PointsPerFiveSecondsKpi pointsPerFiveSecondsKpi = new PointsPerFiveSecondsKpi();
	private final SquaresPerFiveSecondsKpi squaresPerFiveSecondsKpi =
	    new SquaresPerFiveSecondsKpi();
	private final TotalCalcSetPointsKpi totalCalcSetPointsKpi = new TotalCalcSetPointsKpi();

	private final TotalTransceiverMessagesKpi totalTransceiverMessagesKpi =
	    new TotalTransceiverMessagesKpi();
	private final TransceiverMessagesPerFiveSecondsKpi transceiverMessagesPerFiveSecondsKpi =
	    new TransceiverMessagesPerFiveSecondsKpi();

	public MonitorThread()
	{
		kpis.add(avgSquareCalcTimeKpi);
		kpis.add(avgPointCalcTimeKpi);
		kpis.add(avgCalcPointsInSquareKpi);
		kpis.add(avgIterationsBySetPointKpi);
		kpis.add(totalCalculationTimeInSecondsKpi);
		kpis.add(totalSquaresToCalculateKpi);
		kpis.add(totalProcessedSquaresKpi);
		kpis.add(totalGuessedSquaresKpi);
		kpis.add(pointsPerFiveSecondsKpi);
		kpis.add(squaresPerFiveSecondsKpi);
		kpis.add(totalCalcSetPointsKpi);
		kpis.add(totalTransceiverMessagesKpi);
		kpis.add(transceiverMessagesPerFiveSecondsKpi);
	}

	public MonitorThread(int loggingMode)
	{
		this();
		this.loggingMode = loggingMode;
	}

	public void registerAdditionalKpi(Kpi kpi)
	{
		if (!(kpi instanceof AdditionalKpi)) return;
		kpis.add(kpi);
		additionalKpis.add((AdditionalKpi) kpi);
	}

	@Override
	public void run()
	{
		logger.info("MonitorThread started");
		while (canRun())
		{
			try
			{
				Thread.sleep(5000);
				updateKpis();
				logKpiValues();
			}
			catch (InterruptedException e)
			{
			}
		}
		stopped = true;
		logger.info("MonitorThread terminated");
	}

	private void updateKpis()
	{
		for (Kpi kpi : kpis)
		{
			kpi.updateValue();
		}
	}

	private void logKpiValues()
	{
		logger.info("------------------------------------------------------------");
		if ((loggingMode & MonitorLoggingMode.CALC) != 0)
		{
			logger.info(String.format("AVG calc time millis:  square = %f, point = %f",
			        avgSquareCalcTimeKpi.getValue(), avgPointCalcTimeKpi.getValue()));
			logger.info(String.format("AVG points in square = %d, AVG iter by set point = %d",
			        avgCalcPointsInSquareKpi.getValue(), avgIterationsBySetPointKpi.getValue()));
			logger.info(String.format("TOTAL SQUARES processed = %d (guessed = %d), total = %d",
			        totalProcessedSquaresKpi.getValue(), totalGuessedSquaresKpi.getValue(),
			        totalSquaresToCalculateKpi.getValue()));
			logger.info(String.format("TOTAL calc set points = %d",
			        totalCalcSetPointsKpi.getValue()));
			logger.info(String.format("PER 5 SECONDS calc squares = %d, points = %d",
			        squaresPerFiveSecondsKpi.getValue(), pointsPerFiveSecondsKpi.getValue()));
			logger.info(String.format("TOTAL time seconds = %f",
			        totalCalculationTimeInSecondsKpi.getValue()));
		}
		if ((loggingMode & MonitorLoggingMode.TRANSCEIVER) != 0)
		{
			logger.info(String.format("TRANSCEIVER MESSGAES total = %d, per 5 seconds = %d",
			        totalTransceiverMessagesKpi.getValue(),
			        transceiverMessagesPerFiveSecondsKpi.getValue()));
		}
		// Log memory usage every time.
		double totalMemMB = ((double) Runtime.getRuntime().totalMemory()) / 1024 / 1024;
		double freeMemMB = ((double) Runtime.getRuntime().freeMemory()) / 1024 / 1024;
		double usedMemMB = totalMemMB - freeMemMB;
		logger.info(String.format("MEMORY USAGE MB total: %6.1f; used: %6.1f; free: %6.1f",
		        totalMemMB, usedMemMB, freeMemMB));

		logAdditionalKpis();
	}

	private void logAdditionalKpis()
	{
		for (AdditionalKpi kpi : additionalKpis)
		{
			kpi.logValue(logger);
		}
	}
}
