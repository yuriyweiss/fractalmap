package fractal.map.calc.request;

import fractal.map.calc.CalcUtils;
import fractal.map.message.request.PointCoordsRequest;
import fractal.map.message.response.PointCoordsResponse;
import fractal.map.model.Layer;
import fractal.map.model.LayerRegistry;

public class PointCoordsCalculator
{
	private final PointCoordsRequest request;
	private Layer layer;

	public PointCoordsCalculator(PointCoordsRequest request)
	{
		this.request = request;
	}

	public PointCoordsResponse calculate()
	{
		layer = LayerRegistry.getLayerByIndex(request.getLayerIndex());
		double shiftedRe =
		    CalcUtils.limitRe(request.getRe() + request.getShiftX() * layer.getPointWidth());
		double shiftedIm =
		    CalcUtils.limitIm(request.getIm() + request.getShiftY() * layer.getPointWidth());
		return new PointCoordsResponse(request.getRequestUUID(), shiftedRe, shiftedIm);
	}
}
