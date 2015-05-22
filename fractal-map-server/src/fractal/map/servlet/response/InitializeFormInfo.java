package fractal.map.servlet.response;

import java.util.ArrayList;
import java.util.List;

import fractal.map.calc.Constants;

public class InitializeFormInfo extends AbstractInfo
{
	private final double centerRe;
	private final double centerIm;
	private final int currentLayerIndex;
	private final int squareSideSize;
	private List<LayerInfo> layers = new ArrayList<LayerInfo>();

	public InitializeFormInfo(double centerRe, double centerIm, int currentLayerIndex)
	{
		this.centerRe = centerRe;
		this.centerIm = centerIm;
		this.currentLayerIndex = currentLayerIndex;
		this.squareSideSize = Constants.SQUARE_SIDE_SIZE;
	}

	public double getCenterRe()
	{
		return centerRe;
	}

	public double getCenterIm()
	{
		return centerIm;
	}

	public List<LayerInfo> getLayers()
	{
		return layers;
	}

	public void addLayer(LayerInfo layerInfo)
	{
		layers.add(layerInfo);
	}

	public int getCurrentLayerIndex()
	{
		return currentLayerIndex;
	}

	public int getSquareSideSize()
	{
		return squareSideSize;
	}
}
