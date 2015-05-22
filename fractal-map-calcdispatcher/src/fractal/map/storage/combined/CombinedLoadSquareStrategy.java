package fractal.map.storage.combined;

import fractal.map.calc.LoadSquareStrategy;
import fractal.map.conf.Configuration;
import fractal.map.model.Square;
import fractal.map.storage.disk.LoadSquareFromDiskStrategy;
import fractal.map.storage.mysql.LoadSquareFromMySQLStrategy;

public class CombinedLoadSquareStrategy implements LoadSquareStrategy
{
	private final String rootDir;

	public CombinedLoadSquareStrategy()
	{
		this.rootDir = Configuration.getStorageDiskRootDir();
	}

	public CombinedLoadSquareStrategy(String rootDir)
	{
		this.rootDir = rootDir;
	}

	@Override
	public Square loadSquare(int layerIndex, double leftRe, double topIm) throws Exception
	{
		return new LoadSquareFromMySQLStrategy().loadSquare(layerIndex, leftRe, topIm);
	}

	@Override
	public byte[] loadSquareBody(Square square) throws Exception
	{
		return new LoadSquareFromDiskStrategy(rootDir).loadSquareBody(square);
	}
}
