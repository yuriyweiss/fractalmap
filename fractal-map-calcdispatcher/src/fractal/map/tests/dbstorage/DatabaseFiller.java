package fractal.map.tests.dbstorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractal.map.conf.Configuration;
import fractal.map.monitor.MonitorThread;
import fractal.map.storage.oracle.ConnectionPool;
import fractal.map.tests.dbstorage.calc.FillDbTask;

public class DatabaseFiller
{
	private static final Logger logger = Logger.getLogger(DatabaseFiller.class);

	public static void main(String[] args) throws Exception
	{
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!
		// FIXME obsolete, not working
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!
		PropertyConfigurator.configure("conf/load-test-log4j.conf");
		Configuration.load("conf/load-test.conf");

		MonitorThread monitorThread = new MonitorThread();

		Connection conn = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		try
		{
			conn = ConnectionPool.getInstance().getConnection();
			conn.setAutoCommit(false);
			clearLayerSquares(conn);
			Future<?> future = executor.submit(new FillDbTask(conn));
			monitorThread.start();
			try
			{
				future.get();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			monitorThread.terminate();
			executor.shutdown();
			if (conn != null)
			{
				conn.close();
			}
			ConnectionPool.getInstance().close();
		}

		logger.info("application stopped");
	}

	private static void clearLayerSquares(Connection conn) throws SQLException
	{
		PreparedStatement stmt = null;
		try
		{
			String sql = "DELETE FROM SQUARE WHERE LAYER_INDEX = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, Configuration.getLayerIndex());
			stmt.executeUpdate();
			conn.commit();
		}
		catch (SQLException e)
		{
			logger.error(e.getMessage(), e);
			conn.rollback();
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}
}
