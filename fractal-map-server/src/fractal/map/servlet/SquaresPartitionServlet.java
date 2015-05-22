package fractal.map.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fractal.map.message.ServletMessage;
import fractal.map.message.request.AreaSquarePartitionRequest;
import fractal.map.message.response.AreaSquarePartitionResponse;
import fractal.map.message.response.SquareInfo;
import fractal.map.servlet.response.SquarePartitionInfo;
import fractal.map.transceiver.Transportable;

@WebServlet(name = "squares-partition", urlPatterns = { "/squares-partition" })
public class SquaresPartitionServlet extends AbstractResponseWaitingServlet
{
	private static final long serialVersionUID = 1L;

	public SquaresPartitionServlet()
	{
		super();
	}

	@Override
	protected ServletMessage createMessage(HttpServletRequest request, HttpSession session)
	{
		Double centerRe = (Double) session.getAttribute(Names.CENTER_RE);
		Double centerIm = (Double) session.getAttribute(Names.CENTER_IM);
		Integer currentLayerIndex = (Integer) session.getAttribute(Names.CURRENT_LAYER_INDEX);
		int areaWidth = Integer.parseInt(request.getParameter(Names.AREA_WIDTH));
		int areaHeight = Integer.parseInt(request.getParameter(Names.AREA_HEIGHT));
		AreaSquarePartitionRequest message =
		    new AreaSquarePartitionRequest(UUID.randomUUID(), currentLayerIndex, centerRe, centerIm, areaWidth, areaHeight);
		return message;
	}

	@Override
	protected void writeErrorResponse(HttpServletResponse response, ServletError error)
	        throws IOException
	{
		SquarePartitionInfo squarePartitionInfo = new SquarePartitionInfo();
		squarePartitionInfo.setWasError(true);
		squarePartitionInfo.setError(error);
		writeResponse(response, squarePartitionInfo);
	}

	@Override
	protected void writeReplyMessageToResponse(HttpServletResponse response,
	        Transportable replyMessage) throws IOException
	{
		AreaSquarePartitionResponse message = (AreaSquarePartitionResponse) replyMessage;
		SquarePartitionInfo squarePartitionInfo = new SquarePartitionInfo();
		for (SquareInfo squareInfo : message.getSquares())
		{
			squarePartitionInfo.addSquare(squareInfo.getLeftRe(), squareInfo.getTopIm(),
			        squareInfo.getCanvasLeftX(), squareInfo.getCanvasTopY());
		}
		writeResponse(response, squarePartitionInfo);
	}
}
