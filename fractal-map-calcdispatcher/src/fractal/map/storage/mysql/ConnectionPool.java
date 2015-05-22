package fractal.map.storage.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import fractal.map.conf.Configuration;

public class ConnectionPool
{
	private PoolingDataSource<PoolableConnection> dataSource;
	private ObjectPool<PoolableConnection> connectionPool;

	private static ConnectionPool instance = null;

	public static ConnectionPool getInstance() throws Exception
	{
		if (instance == null)
		{
			instance = new ConnectionPool();
		}
		return instance;
	}

	private ConnectionPool() throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		String connectionString = Configuration.getStorageMySQLConnectionString();
		Properties props = new Properties();
		props.setProperty("user", Configuration.getStorageMySQLConnectionUser());
		props.setProperty("password", Configuration.getStorageMySQLConnectionPassword());
		ConnectionFactory connectionFactory =
		    new DriverManagerConnectionFactory(connectionString, props);

		PoolableConnectionFactory poolableConnectionFactory =
		    new PoolableConnectionFactory(connectionFactory, null);
		poolableConnectionFactory.setDefaultAutoCommit(false);

		connectionPool = new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
		((GenericObjectPool<PoolableConnection>) connectionPool).setMaxTotal(Configuration.getStorageMySQLConnectionPoolSize());
		poolableConnectionFactory.setPool(connectionPool);

		dataSource = new PoolingDataSource<PoolableConnection>(connectionPool);
	}

	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

	public void close()
	{
		connectionPool.close();
	}
}
