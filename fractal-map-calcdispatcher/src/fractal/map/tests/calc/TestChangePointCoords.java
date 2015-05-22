package fractal.map.tests.calc;

import java.util.UUID;

import fractal.map.calc.request.PointCoordsCalculator;
import fractal.map.message.MessagesRegistrator;
import fractal.map.message.request.PointCoordsRequest;
import fractal.map.message.response.PointCoordsResponse;

public class TestChangePointCoords
{
	public static void main(String[] args)
	{
		MessagesRegistrator.registerMessages();
		double re = 0;
		double im = 0;
		int shiftX = -100;
		int shiftY = -10;
		PointCoordsRequest request =
		    new PointCoordsRequest(UUID.randomUUID(), 1, re, im, shiftX, shiftY);
		PointCoordsResponse response = new PointCoordsCalculator(request).calculate();
		double newRe = -0.29296875;
		double newIm = -0.029296875;
		System.out.println(String.format("estimated [re: %11.10f; im: %11.10f]", newRe, newIm));
		System.out.println(String.format("calculated [re: %11.10f; im: %11.10f]", response.getRe(),
		        response.getIm()));
	}
}
