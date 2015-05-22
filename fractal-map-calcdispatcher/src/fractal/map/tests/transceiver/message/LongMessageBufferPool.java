package fractal.map.tests.transceiver.message;

import org.apache.commons.pool2.ObjectPool;

import fractal.map.util.buffer.Buffer;
import fractal.map.util.buffer.BufferPoolBuilder;

public class LongMessageBufferPool
{
	private static ObjectPool<Buffer> bufferPool;

	public static void init(int bufferSize, int poolSize)
	{
		bufferPool = BufferPoolBuilder.build(bufferSize, poolSize);
	}

	public static Buffer borrowObject() throws Exception
	{
		return bufferPool.borrowObject();
	}

	public static void returnObject(Buffer buffer) throws Exception
	{
		bufferPool.returnObject(buffer);
	}
}
