package fractal.map.transceiver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import fractal.map.conf.Configuration;
import fractal.map.util.StoppableThread;

public class ServerSocketThread extends StoppableThread
{
	private final static Logger logger = Logger.getLogger(ServerSocketThread.class);

	private final TransceiverServer owner;
	private final int selectionMode;

	public ServerSocketThread(TransceiverServer owner, int selectionMode)
	{
		super();
		this.owner = owner;
		this.selectionMode = selectionMode;
	}

	private int getPort()
	{
		return (selectionMode == SelectionKey.OP_READ) ? owner.getReadPort() : owner.getWritePort();
	}

	@Override
	public void run()
	{
		while (canRun())
		{
			boolean error = false;
			try
			{
				ServerSocketChannel serverChannel = ServerSocketChannel.open();
				int port = getPort();
				serverChannel.bind(new InetSocketAddress(port));
				logger.info("server socket bound to port: " + port);
				while (canRun())
				{
					SocketChannel socketChannel = serverChannel.accept();
					owner.createNewWorker(selectionMode, socketChannel);
				}
			}
			catch (IOException e)
			{
				if (!(e instanceof ClosedByInterruptException))
				{
					error = true;
					logger.error("server socket error");
					logger.debug("error stack: ", e);
				}
				else
				{
					logger.error("server socket interrupted");
				}
			}
			if (error)
			{
				try
				{
					int interval = Configuration.getTransceiverErrorIntervalSeconds();
					logger.info("waiting " + interval + " seconds before resurrect attempt");
					Thread.sleep(interval * 1000L);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
		stopped = true;
		logger.info("server socket thread stopped");
	}
}
