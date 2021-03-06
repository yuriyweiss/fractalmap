package fractal.map.calc.square.point;

import fractal.map.conf.Configuration;

public class PointCalculationTaskFactory
{
	public static AbstractPointCalculationTask createTask(int[][] originalPoints, int x, int y, double re,
	        double im, PointCalcFinishedListener calcFinishedListener)
	{
		if (Configuration.isPointOptimizationActive())
		{
			if ("uniform".equals(Configuration.getPointOptimizationType()))
			{
				return new PointCalculationTaskOptimizedUniform(originalPoints, x, y, re, im, calcFinishedListener);
			}
			else if ("geometric".equals(Configuration.getPointOptimizationType()))
			{
				return new PointCalculationTaskOptimizedGeometric(originalPoints, x, y, re, im, calcFinishedListener);
			}
			else
			{
				throw new RuntimeException("ERROR config parameter point.optimization.type UNKNOWN value ["
				        + Configuration.getPointOptimizationType() + "]");
			}
		}
		else
		{
			return new PointCalculationTaskSimple(originalPoints, x, y, re, im, calcFinishedListener);
		}
	}
}
