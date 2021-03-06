package fractal.map.storage.disk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fractal.map.model.Layer;
import fractal.map.model.Square;

public class FileSystemHelper
{
	private final Square square;
	private final String rootDir;
	private final List<String> dirStructure;

	public FileSystemHelper(Square square, String rootDir)
	{
		this.square = square;
		this.rootDir = rootDir;
		this.dirStructure = buildDirStructure(square);
	}

	private List<String> buildDirStructure(Square square)
	{
		List<String> result = new ArrayList<String>();
		Layer layer = square.getLayer();
		result.add("layer_" + layer.getLayerIndex());
		long squareIndex = square.getSquareIndex();

		long squareCount = layer.getSquaresBySide() * layer.getSquaresBySide() / 2;
		long maxThousandPower = 1000;
		while (maxThousandPower < squareCount)
		{
			maxThousandPower *= 1000;
		}

		if (maxThousandPower == 1000)
		{
			result.add("000");
		}
		else
		{
			long remainder = squareIndex;
			long divided = squareIndex;
			do
			{
				maxThousandPower /= 1000;
				divided = remainder / maxThousandPower;
				result.add(String.format("%03d", divided));
				remainder = remainder % maxThousandPower;
			}
			while (maxThousandPower > 1000);
		}

		return result;
	}

	public String getFileName()
	{
		return getDirNameForSquare() + "/" + square.getSquareIndex() + "_x_" + square.getLayerX()
		        + "_y_" + square.getLayerY() + ".dat";
	}

	private String getDirNameForSquare()
	{
		return buildDirName(dirStructure);
	}

	private String buildDirName(List<String> dirStructure)
	{
		String result = rootDir;
		for (String dirName : dirStructure)
		{
			result += "/" + dirName;
		}
		return result;
	}

	public void createDirectories()
	{
		String currentDirName = rootDir;
		for (String dirName : dirStructure)
		{
			currentDirName += "/" + dirName;
			if (!isDirExists(currentDirName))
			{
				File newDir = new File(currentDirName);
				newDir.mkdir();
			}
		}
	}

	private boolean isDirExists(String dirName)
	{
		return new File(dirName).exists();
	}
}
