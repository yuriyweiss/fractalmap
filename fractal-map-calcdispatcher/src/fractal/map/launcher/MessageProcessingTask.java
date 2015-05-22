package fractal.map.launcher;

import fractal.map.calc.LoadSquareStrategy;
import fractal.map.calc.SaveSquareStrategy;
import fractal.map.calc.request.PointCoordsCalculator;
import fractal.map.calc.request.SquareCalculator;
import fractal.map.calc.request.SquaresPartitionCalculator;
import fractal.map.message.MessagesRegistrator;
import fractal.map.message.ServletMessage;
import fractal.map.message.request.AreaSquarePartitionRequest;
import fractal.map.message.request.PointCoordsRequest;
import fractal.map.message.request.SquareRequest;
import fractal.map.storage.combined.CombinedLoadSquareStrategy;
import fractal.map.storage.combined.CombinedSaveSquareStrategy;
import fractal.map.transceiver.server.ServerMessage;
import fractal.map.transceiver.server.TransceiverServer;

public class MessageProcessingTask implements Runnable
{
	private final ServerMessage message;
	private final TransceiverServer transceiverServer;

	public MessageProcessingTask(ServerMessage message, TransceiverServer transceiverServer)
	{
		this.message = message;
		this.transceiverServer = transceiverServer;
	}

	@Override
	public void run()
	{
		ServletMessage servletMessage = (ServletMessage) message.getBody();
		ServletMessage response = null;
		switch (servletMessage.getClassId())
		{
			case MessagesRegistrator.REQUEST_AREA_SQUARE_PARTITION:
				response =
				    new SquaresPartitionCalculator((AreaSquarePartitionRequest) message.getBody()).calculate();
				break;
			case MessagesRegistrator.REQUEST_POINT_COORDS:
				response =
				    new PointCoordsCalculator((PointCoordsRequest) message.getBody()).calculate();
				break;
			case MessagesRegistrator.REQUEST_SQUARE:
				LoadSquareStrategy loadStrategy = new CombinedLoadSquareStrategy();
				SaveSquareStrategy saveStrategy = new CombinedSaveSquareStrategy();
				response =
				    new SquareCalculator((SquareRequest) message.getBody(), loadStrategy, saveStrategy).calculate();
				break;
		}
		if (response != null)
		{
			transceiverServer.send(new ServerMessage(message.getClientUUID(), response));
		}
	}

	public ServerMessage getMessage()
	{
		return message;
	}
}
