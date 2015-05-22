package fractal.map.tests.calc;

import java.util.UUID;

import org.apache.log4j.PropertyConfigurator;

import fractal.map.calc.request.SquareCalculator;
import fractal.map.conf.Configuration;
import fractal.map.message.request.SquareRequest;
import fractal.map.storage.combined.CombinedLoadSquareStrategy;
import fractal.map.storage.combined.CombinedSaveSquareStrategy;

public class TestRequestSquare
{
	public static void main(String[] args) throws Exception
	{
		PropertyConfigurator.configure("conf/calcdisp-log4j.conf");
		Configuration.load("conf/calcdisp.conf");

		// Test calculation or DB load of upper square.
		int layerIndex = 3;
		double leftRe = -0.59375;
		double topIm = 0.46875;
		SquareRequest request = new SquareRequest(UUID.randomUUID(), layerIndex, leftRe, topIm);
		new SquareCalculator(request, new CombinedLoadSquareStrategy(), new CombinedSaveSquareStrategy()).calculate();

		// Test coordinates symmetric conversion (from negative IM to positive IM).
		layerIndex = 3;
		// x = 17 (0 - first index)
		// y from top = 9 (0 - first index) => y = 32-9 = 23
		leftRe = 0.09375 * 17 - 2;
		topIm = 1.5 - 0.09375 * 22;
		request = new SquareRequest(UUID.randomUUID(), layerIndex, leftRe, topIm);
		new SquareCalculator(request, new CombinedLoadSquareStrategy(), new CombinedSaveSquareStrategy()).calculate();
	}
}
