package fractal.map.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fractal.map.message.ServletMessage;
import fractal.map.message.request.PointCoordsRequest;
import fractal.map.message.response.AreaSquarePartitionResponse;
import fractal.map.message.response.PointCoordsResponse;
import fractal.map.message.response.SquareInfo;
import fractal.map.servlet.response.PointCoordsInfo;
import fractal.map.servlet.response.SquarePartitionInfo;
import fractal.map.transceiver.Transportable;

@WebServlet(name = "get-point-coords", urlPatterns = { "/get-point-coords" })
public class GetPointCoordsServlet extends AbstractResponseWaitingServlet
{
	private static final long serialVersionUID = 1L;

	public GetPointCoordsServlet() {
		super();
	}

	@Override
	protected ServletMessage createMessage(HttpServletRequest request, HttpSession session)
	{
		Integer layerIndex = (Integer) session.getAttribute(Names.CURRENT_LAYER_INDEX);
		Double re = (Double) session.getAttribute(Names.CENTER_RE);
		Double im = (Double) session.getAttribute(Names.CENTER_IM);
		int shiftX = Integer.parseInt(request.getParameter(Names.SHIFT_X));
		int shiftY = Integer.parseInt(request.getParameter(Names.SHIFT_Y));
		PointCoordsRequest message = new PointCoordsRequest(UUID.randomUUID(), layerIndex, re, im, shiftX, shiftY);
		return message;
	}

	@Override
	protected void writeErrorResponse(HttpServletResponse response, ServletError error)
	        throws IOException
	{
		PointCoordsInfo pointCoordsInfo = new PointCoordsInfo();
		pointCoordsInfo.setWasError(true);
		pointCoordsInfo.setError(error);
		writeResponse(response, pointCoordsInfo);
	}

	@Override
	protected void writeReplyMessageToResponse(HttpServletResponse response,
	        Transportable replyMessage) throws IOException
	{
		PointCoordsResponse message = (PointCoordsResponse) replyMessage;
		PointCoordsInfo pointCoordsInfo = new PointCoordsInfo(message.getRe(), message.getIm());
		writeResponse(response, pointCoordsInfo);
	}
}
