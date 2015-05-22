package fractal.map.servlet.response;

public class SquareBodyInfo extends AbstractInfo
{
	private int iterations = -2;

	public SquareBodyInfo()
	{
		super();
	}

	public SquareBodyInfo(int iterations)
	{
		super();
		this.iterations = iterations;
	}

	public int getIterations()
	{
		return iterations;
	}
}
