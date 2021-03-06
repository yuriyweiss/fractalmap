package fractal.map.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	private static int transceiverReadPort = 15021;
	private static int transceiverWritePort = 15022;
	private static int transceiverBufferSize = 1048576; // 1 Mb
	private static int transceiverErrorIntervalSeconds = 5;

	private static String storageDiskRootDir = null;
	private static int storageDiskBufferSize = 262144; // 256 Kb

	private static int storageMySQLConnectionPoolSize = 10;
	private static String storageMySQLConnectionString = null;
	private static String storageMySQLConnectionUser = "fractalmap";
	private static String storageMySQLConnectionPassword = "fractalmap";

	private static int storageOracleConnectionPoolSize = 10;
	private static String storageOracleConnectionString = null;
	private static String storageOracleConnectionUser = "fractalmap";
	private static String storageOracleConnectionPassword = "fractalmap";

	private static int layerIndex = 4;
	private static long layerSideSize = 65536;
	private static int iterationsCount = 5000;

	private static int calcSquareThreadsCount = 4;

	private static boolean pointOptimizationActive = false;
	private static int pointOptimizationBufferSize = 50;
	private static String pointOptimizationType = "uniform";
	private static int pointOptimizationUniformCheckCount = 2;
	private static double pointOptimizationGeomIntervalMagnifier = 3;
	private static int pointOptimizationGeomFirstInterval = 100;

	private static boolean squareOptimizationActive = false;
	private static int squareOptimizationSideSizeLimit = 16;

	public static void load(String fileName) throws FileNotFoundException, IOException,
	        ParseException
	{
		Properties properties = new Properties();
		properties.load(new FileInputStream(fileName));

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.setDecimalFormatSymbols(dfs);

		transceiverReadPort =
		    Integer.parseInt(properties.getProperty("transceiver.read.port", "15021"));
		transceiverWritePort =
		    Integer.parseInt(properties.getProperty("transceiver.write.port", "15022"));
		transceiverBufferSize =
		    Integer.parseInt(properties.getProperty("transceiver.buffer.size", "1048576"));
		transceiverErrorIntervalSeconds =
		    Integer.parseInt(properties.getProperty("transceiver.error.interval.seconds", "5"));

		storageDiskRootDir = properties.getProperty("storage.disk.root.dir", null);
		storageDiskBufferSize =
		    Integer.parseInt(properties.getProperty("storage.disk.buffer.size", "262144"));

		storageMySQLConnectionPoolSize =
		    Integer.parseInt(properties.getProperty("storage.mysql.connection.pool.size", "10"));
		storageMySQLConnectionString =
		    properties.getProperty("storage.mysql.connection.string", null);
		storageMySQLConnectionUser =
		    properties.getProperty("storage.mysql.connection.user", "fractalmap");
		storageMySQLConnectionPassword =
		    properties.getProperty("storage.mysql.connection.password", "fractalmap");

		storageOracleConnectionPoolSize =
		    Integer.parseInt(properties.getProperty("storage.oracle.connection.pool.size", "10"));
		storageOracleConnectionString =
		    properties.getProperty("storage.oracle.connection.string", null);
		storageOracleConnectionUser =
		    properties.getProperty("storage.oracle.connection.user", "fractalmap");
		storageOracleConnectionPassword =
		    properties.getProperty("storage.oracle.connection.password", "fractalmap");

		layerIndex = Integer.parseInt(properties.getProperty("layer.index", "4"));
		layerSideSize = Integer.parseInt(properties.getProperty("layer.side.size", "65536"));
		iterationsCount = Integer.parseInt(properties.getProperty("iterations.count", "5000"));

		calcSquareThreadsCount =
		    Integer.parseInt(properties.getProperty("calc.square.threads.count", "4"));

		pointOptimizationActive =
		    Boolean.parseBoolean(properties.getProperty("point.optimization.active", "false"));
		pointOptimizationBufferSize =
		    Integer.parseInt(properties.getProperty("point.optimization.buffer.size", "50"));
		pointOptimizationType = properties.getProperty("point.optimization.type", "uniform");
		pointOptimizationUniformCheckCount =
		    Integer.parseInt(properties.getProperty("point.optimization.uniform.check.count", "2"));
		pointOptimizationGeomIntervalMagnifier =
		    df.parse(properties.getProperty("point.optimization.geom.interval.magnifier", "3")).doubleValue();
		pointOptimizationGeomFirstInterval =
		    Integer.parseInt(properties.getProperty("point.optimization.geom.first.interval", "100"));

		squareOptimizationActive =
		    Boolean.parseBoolean(properties.getProperty("square.optimization.active", "false"));
		squareOptimizationSideSizeLimit =
		    Integer.parseInt(properties.getProperty("square.optimization.side.size.limit", "16"));

		logger.info("configuration loaded");
		logConfiguration();
	}

	private static void logConfiguration()
	{
		logger.info("transceiver.read.port: " + transceiverReadPort);
		logger.info("transceiver.write.port: " + transceiverWritePort);
		logger.info("transceiver.buffer.size: " + transceiverBufferSize);
		logger.info("transceiver.error.interval.seconds: " + transceiverErrorIntervalSeconds);

		logger.info("storage.disk.root.dir: " + storageDiskRootDir);
		logger.info("storage.disk.buffer.size: " + storageDiskBufferSize);

		logger.info("storage.mysql.connection.pool.size: " + storageMySQLConnectionPoolSize);
		logger.info("storage.mysql.connection.string: " + storageMySQLConnectionString);
		logger.info("storage.mysql.connection.user: " + storageMySQLConnectionUser);
		logger.info("storage.mysql.connection.password: " + storageMySQLConnectionPassword);

		logger.info("storage.oracle.connection.pool.size: " + storageOracleConnectionPoolSize);
		logger.info("storage.oracle.connection.string: " + storageOracleConnectionString);
		logger.info("storage.oracle.connection.user: " + storageOracleConnectionUser);
		logger.info("storage.oracle.connection.password: " + storageOracleConnectionPassword);

		logger.info("layer.index: " + layerIndex);
		logger.info("layer.side.size: " + layerSideSize);
		logger.info("iterations.count: " + iterationsCount);

		logger.info("calc.square.threads.count: " + calcSquareThreadsCount);

		logger.info("point.optimization.active: " + pointOptimizationActive);
		logger.info("point.optimization.type: " + pointOptimizationType);
		logger.info("point.optimization.buffer.size: " + pointOptimizationBufferSize);
		logger.info("point.optimization.uniform.check.count: " + pointOptimizationUniformCheckCount);
		logger.info("point.optimization.geom.interval.magnifier: "
		        + pointOptimizationGeomIntervalMagnifier);
		logger.info("point.optimization.geom.first.interval: " + pointOptimizationGeomFirstInterval);

		logger.info("square.optimization.active: " + squareOptimizationActive);
		logger.info("square.optimization.side.size.limit: " + squareOptimizationSideSizeLimit);
	}

	public static int getLayerIndex()
	{
		return layerIndex;
	}

	public static long getLayerSideSize()
	{
		return layerSideSize;
	}

	public static int getIterationsCount()
	{
		return iterationsCount;
	}

	public static boolean isPointOptimizationActive()
	{
		return pointOptimizationActive;
	}

	public static int getPointOptimizationBufferSize()
	{
		return pointOptimizationBufferSize;
	}

	public static boolean isSquareOptimizationActive()
	{
		return squareOptimizationActive;
	}

	public static int getCalcSquareThreadsCount()
	{
		return calcSquareThreadsCount;
	}

	public static int getPointOptimizationUniformCheckCount()
	{
		return pointOptimizationUniformCheckCount;
	}

	public static String getPointOptimizationType()
	{
		return pointOptimizationType;
	}

	public static double getPointOptimizationGeomIntervalMagnifier()
	{
		return pointOptimizationGeomIntervalMagnifier;
	}

	public static int getPointOptimizationGeomFirstInterval()
	{
		return pointOptimizationGeomFirstInterval;
	}

	public static int getSquareOptimizationSideSizeLimit()
	{
		return squareOptimizationSideSizeLimit;
	}

	public static String getStorageOracleConnectionString()
	{
		return storageOracleConnectionString;
	}

	public static String getStorageOracleConnectionUser()
	{
		return storageOracleConnectionUser;
	}

	public static String getStorageOracleConnectionPassword()
	{
		return storageOracleConnectionPassword;
	}

	public static int getTransceiverBufferSize()
	{
		return transceiverBufferSize;
	}

	public static int getTransceiverErrorIntervalSeconds()
	{
		return transceiverErrorIntervalSeconds;
	}

	public static int getTransceiverReadPort()
	{
		return transceiverReadPort;
	}

	public static int getTransceiverWritePort()
	{
		return transceiverWritePort;
	}

	public static String getStorageDiskRootDir()
	{
		return storageDiskRootDir;
	}

	public static int getStorageDiskBufferSize()
	{
		return storageDiskBufferSize;
	}

	public static String getStorageMySQLConnectionString()
	{
		return storageMySQLConnectionString;
	}

	public static String getStorageMySQLConnectionUser()
	{
		return storageMySQLConnectionUser;
	}

	public static String getStorageMySQLConnectionPassword()
	{
		return storageMySQLConnectionPassword;
	}

	public static int getStorageMySQLConnectionPoolSize()
	{
		return storageMySQLConnectionPoolSize;
	}

	public static int getStorageOracleConnectionPoolSize()
	{
		return storageOracleConnectionPoolSize;
	}
}
