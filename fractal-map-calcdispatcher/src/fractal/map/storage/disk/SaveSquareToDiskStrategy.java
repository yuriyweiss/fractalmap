package fractal.map.storage.disk;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import fractal.map.calc.CalcUtils;
import fractal.map.calc.Constants;
import fractal.map.calc.SaveSquareStrategy;
import fractal.map.conf.Configuration;
import fractal.map.model.Square;

public class SaveSquareToDiskStrategy implements SaveSquareStrategy
{
	private static final Logger logger = Logger.getLogger(SaveSquareToDiskStrategy.class);

	private final String rootDir;

	public SaveSquareToDiskStrategy()
	{
		this.rootDir = Configuration.getStorageDiskRootDir();
	}

	public SaveSquareToDiskStrategy(String rootDir)
	{
		this.rootDir = rootDir;
	}

	@Override
	public void save(Square square, int[][] points) throws Exception
	{
		if (square.getIterations() != Constants.ITERATIONS_DIFFER) return;

		File squareFile = getSquareFile(square);
		FileOutputStream fos = new FileOutputStream(squareFile, false);
		CalcUtils.saveAndCompressResultToStream(fos, points);
	}

	@Override
	public void save(Square square, byte[] squareBody) throws Exception
	{
		if (square.getIterations() != Constants.ITERATIONS_DIFFER) return;

		File squareFile = getSquareFile(square);
		FileOutputStream fos = new FileOutputStream(squareFile, false);
		CalcUtils.saveSquareBodyToStream(fos, squareBody);
	}

	private File getSquareFile(Square square)
	{
		FileSystemHelper fsHelper = new FileSystemHelper(square, rootDir);
		fsHelper.createDirectories();
		String fileName = fsHelper.getFileName();
		logger.debug("saving square body to file: " + fileName);
		File squareFile = new File(fileName);
		return squareFile;
	}
}
