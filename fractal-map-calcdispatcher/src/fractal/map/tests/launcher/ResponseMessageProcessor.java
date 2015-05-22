package fractal.map.tests.launcher;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import fractal.map.context.Context;
import fractal.map.message.MessagesRegistrator;
import fractal.map.message.ServletMessage;
import fractal.map.message.request.AreaSquarePartitionRequest;
import fractal.map.message.request.SquareRequest;
import fractal.map.message.response.AreaSquarePartitionResponse;
import fractal.map.message.response.PointCoordsResponse;
import fractal.map.message.response.SquareInfo;
import fractal.map.transceiver.MessageProcessor;
import fractal.map.transceiver.Transportable;
import fractal.map.transceiver.client.TransceiverClient;
import fractal.map.waiterscache.WaitersCache;

public class ResponseMessageProcessor implements MessageProcessor
{
	private static final Logger logger = Logger.getLogger(ResponseMessageProcessor.class);

	private final TransceiverClient transceiverClient;
	private final WaitersCache waiters;

	public ResponseMessageProcessor(TransceiverClient transceiverClient, WaitersCache waiters)
	{
		this.transceiverClient = transceiverClient;
		this.waiters = waiters;
	}

	@Override
	public void onMessageDecoded(UUID clientUUID, Transportable message)
	{
		Context.getBaseCounters().getPeriodicTransceiverMessages().getAndIncrement();
		Context.getBaseCounters().getTotalTransceiverMessages().getAndIncrement();

		ServletMessage response = (ServletMessage) message;
		waiters.onResponseArrived(response.getRequestUUID(), response);
		logger.debug("response arrived: " + response);
		switch (response.getClassId())
		{
			case MessagesRegistrator.RESPONSE_POINT_COORDS:
				processPointCoordsResponse((PointCoordsResponse) response);
				break;
			case MessagesRegistrator.RESPONSE_AREA_SQUARE_PARTITION:
				processAreaSquarePartitionResponse((AreaSquarePartitionResponse) response);
				break;
		}
	}

	private void processPointCoordsResponse(PointCoordsResponse response)
	{
		AreaSquarePartitionRequest request =
		    new AreaSquarePartitionRequest(UUID.randomUUID(), 5, response.getRe(), response.getIm(), 1024, 768);
		//logger.debug("sending request: " + request);
		waiters.put(request.getRequestUUID(), new DummyWaiter());
		transceiverClient.send(request);
	}

	private void processAreaSquarePartitionResponse(AreaSquarePartitionResponse response)
	{
		List<SquareInfo> squares = response.getSquares();
		for (SquareInfo squareInfo : squares)
		{
			SquareRequest request =
			    new SquareRequest(UUID.randomUUID(), 5, squareInfo.getLeftRe(), squareInfo.getTopIm());
			//logger.debug("sending request: " + request);
			waiters.put(request.getRequestUUID(), new DummyWaiter());
			transceiverClient.send(request);
		}
	}
}
