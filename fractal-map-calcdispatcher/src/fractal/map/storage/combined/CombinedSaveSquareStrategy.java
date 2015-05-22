package fractal.map.storage.combined;

import fractal.map.calc.SaveSquareStrategy;
import fractal.map.conf.Configuration;
import fractal.map.model.Square;
import fractal.map.storage.disk.SaveSquareToDiskStrategy;
import fractal.map.storage.mysql.SaveSquareInfoToMySQLStrategy;

public class CombinedSaveSquareStrategy implements SaveSquareStrategy
{
	private final String rootDir;

	public CombinedSaveSquareStrategy()
	{
		this.rootDir = Configuration.getStorageDiskRootDir();
	}

	public CombinedSaveSquareStrategy(String rootDir)
	{
		this.rootDir = rootDir;
	}

	@Override
	public void save(Square square, int[][] points) throws Exception
	{
		new SaveSquareInfoToMySQLStrategy().save(square, points);
		new SaveSquareToDiskStrategy(rootDir).save(square, points);
	}

	@Override
	public void save(Square square, byte[] squareBody) throws Exception
	{
		new SaveSquareInfoToMySQLStrategy().save(square, squareBody);
		new SaveSquareToDiskStrategy(rootDir).save(square, squareBody);
	}
}
