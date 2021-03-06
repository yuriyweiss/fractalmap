package fractal.map.calc;

import fractal.map.model.Square;

public interface SaveSquareStrategy
{
	void save(Square square, int[][] points) throws Exception;

	void save(Square square, byte[] squareBody) throws Exception;
}
