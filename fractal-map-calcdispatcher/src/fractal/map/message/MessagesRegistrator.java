package fractal.map.message;

import fractal.map.message.request.AreaSquarePartitionRequest;
import fractal.map.message.request.PointCoordsRequest;
import fractal.map.message.request.SquareRequest;
import fractal.map.message.response.AreaSquarePartitionResponse;
import fractal.map.message.response.CalcErrorResponse;
import fractal.map.message.response.PointCoordsResponse;
import fractal.map.message.response.SquareResponse;
import fractal.map.transceiver.TransportableFactory;

public class MessagesRegistrator
{
	public static final int REQUEST_POINT_COORDS = 1;
	public static final int RESPONSE_POINT_COORDS = 2;
	public static final int REQUEST_AREA_SQUARE_PARTITION = 3;
	public static final int RESPONSE_AREA_SQUARE_PARTITION = 4;
	public static final int REQUEST_SQUARE = 5;
	public static final int RESPONSE_SQUARE = 6;

	public static final int RESPONSE_CALC_ERROR = 100;

	public static void registerMessages()
	{
		TransportableFactory.registerClass(REQUEST_POINT_COORDS, PointCoordsRequest.class);
		TransportableFactory.registerClass(RESPONSE_POINT_COORDS, PointCoordsResponse.class);
		TransportableFactory.registerClass(REQUEST_AREA_SQUARE_PARTITION,
		        AreaSquarePartitionRequest.class);
		TransportableFactory.registerClass(RESPONSE_AREA_SQUARE_PARTITION,
		        AreaSquarePartitionResponse.class);
		TransportableFactory.registerClass(REQUEST_SQUARE, SquareRequest.class);
		TransportableFactory.registerClass(RESPONSE_SQUARE, SquareResponse.class);
		TransportableFactory.registerClass(RESPONSE_CALC_ERROR, CalcErrorResponse.class);
	}
}
