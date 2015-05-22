package fractal.map.calc.square;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import fractal.map.calc.square.point.AbstractPointCalculationTask;
import fractal.map.conf.Configuration;

public class SquareCalculatorSimple extends AbstractSquareCalculator
{
	public SquareCalculatorSimple(int[][] originalPoints, int leftIndex, int topIndex,
	    int sideSize, double leftRe, double topIm, double width, AbstractSquareCalculator parent)
	{
		super(originalPoints, leftIndex, topIndex, sideSize, leftRe, topIm, width, parent);
	}

	@Override
	protected void performCalculation()
	{
		executor = Executors.newFixedThreadPool(Configuration.getCalcSquareThreadsCount());

		List<AbstractPointCalculationTask> tasksToExecute =
		    new ArrayList<AbstractPointCalculationTask>();
		fillPointCalculationTasks(tasksToExecute);
		pointsCount = tasksToExecute.size();

		for (AbstractPointCalculationTask task : tasksToExecute)
		{
			executor.execute(task);
		}

		synchronized (this)
		{
			while (pointsCount != pointsProcessed)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					executor.shutdown();
				}
			}
		}
	}

	private void fillPointCalculationTasks(List<AbstractPointCalculationTask> tasksToExecute)
	{
		double cellWidth = width / sideSize;
		for (int y = 0; y < sideSize; y++)
		{
			double pointIm = topIm - cellWidth * y - cellWidth / 2;
			for (int x = 0; x < sideSize; x++)
			{
				// Calculate centers of point cells.
				double pointRe = leftRe + cellWidth * x + cellWidth / 2;
				addPointTask(tasksToExecute, x + leftIndex, y + topIndex, pointRe, pointIm);
			}
		}
	}
}
