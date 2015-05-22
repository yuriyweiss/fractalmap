package fractal.map.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fractal.map.calc.Constants;
import fractal.map.message.ServletMessage;
import fractal.map.message.request.SquareRequest;
import fractal.map.message.response.CalcErrorResponse;
import fractal.map.message.response.SquareResponse;
import fractal.map.servlet.response.SquareBodyInfo;
import fractal.map.transceiver.Transportable;

@WebServlet(name = "get-square", urlPatterns = { "/get-square" })
public class GetSquareServlet extends AbstractResponseWaitingServlet
{
	private static final long serialVersionUID = 6274967601501653753L;

	public GetSquareServlet()
	{
		super();
	}

	@Override
	protected ServletMessage createMessage(HttpServletRequest request, HttpSession session)
	{
		double leftRe = Double.parseDouble(request.getParameter(Names.LEFT_RE));
		double topIm = Double.parseDouble(request.getParameter(Names.TOP_IM));
		Integer currentLayerIndex = (Integer) session.getAttribute(Names.CURRENT_LAYER_INDEX);
		SquareRequest message =
		    new SquareRequest(UUID.randomUUID(), currentLayerIndex, leftRe, topIm);
		return message;
	}

	@Override
	protected void writeErrorResponse(HttpServletResponse response, ServletError error)
	        throws IOException
	{
		SquareBodyInfo squareBodyInfo = new SquareBodyInfo();
		squareBodyInfo.setWasError(true);
		squareBodyInfo.setError(error);
		writeResponse(response, squareBodyInfo);
	}

	@Override
	protected void writeReplyMessageToResponse(HttpServletResponse response,
	        Transportable replyMessage) throws IOException
	{
		if (replyMessage instanceof CalcErrorResponse)
		{
			writeErrorResponse(response, ServletError.SQUARE_BODY_NOT_AVAILABLE);
			return;
		}

		SquareResponse message = (SquareResponse) replyMessage;
		if (message.getIterations() != Constants.ITERATIONS_DIFFER)
		{
			SquareBodyInfo squareBodyInfo = new SquareBodyInfo(message.getIterations());
			writeResponse(response, squareBodyInfo);
		}
		else if (message.getSquareBody() != null)
		{
			writeZippedBodyResponse(response, message.getSquareBody());
		}
		else
		{
			writeErrorResponse(response, ServletError.SQUARE_BODY_NOT_AVAILABLE);
		}
	}

	private void writeZippedBodyResponse(HttpServletResponse response, byte[] squareBody)
	        throws IOException
	{
		response.setHeader("Content-Encoding", "gzip");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(squareBody);
	}
}
