package fractal.map.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import fractal.map.message.ServletMessage;
import fractal.map.servlet.app.RequestsCache;
import fractal.map.servlet.app.ServletTransceiverClient;
import fractal.map.transceiver.Transportable;

@SuppressWarnings("serial")
public abstract class AbstractResponseWaitingServlet extends HttpServlet
{
	abstract protected ServletMessage createMessage(HttpServletRequest request, HttpSession session);

	abstract protected void writeErrorResponse(HttpServletResponse response, ServletError error)
	        throws IOException;

	abstract protected void writeReplyMessageToResponse(HttpServletResponse response,
	        Transportable replyMessage) throws IOException;

	public AbstractResponseWaitingServlet()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException
	{
		HttpSession session = request.getSession(false);
		if (session == null)
		{
			writeErrorResponse(response, ServletError.SESSION_NOT_EXISTS);
			return;
		}

		ServletMessage message = createMessage(request, session);
		ServletResponseWaiter responseWaiter = new ServletResponseWaiter();
		RequestsCache.registerWaiter(message.getRequestUUID(), responseWaiter);
		ServletTransceiverClient.send(message);

		boolean interrupted = false;
		synchronized (responseWaiter)
		{
			while (!responseWaiter.isMessageProcessed())
			{
				try
				{
					responseWaiter.wait(5000L);
				}
				catch (InterruptedException e)
				{
					interrupted = true;
					break;
				}
			}
		}

		writeResponseAfterWaiting(response, responseWaiter, interrupted);
	}

	protected void writeResponse(HttpServletResponse response, Object responseInfo)
	        throws IOException
	{
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new Gson().toJson(responseInfo));
	}

	protected void writeResponseAfterWaiting(HttpServletResponse response,
	        ServletResponseWaiter responseWaiter, boolean interrupted) throws IOException
	{
		if (interrupted)
		{
			writeErrorResponse(response, ServletError.MESSAGE_WAITING_INTERRUPTED);
		}
		else if (responseWaiter.isMessageTimeout())
		{
			writeErrorResponse(response, ServletError.MESSAGE_TIMEOUT);
		}
		else if (responseWaiter.getReplyMessage() != null)
		{
			writeReplyMessageToResponse(response, responseWaiter.getReplyMessage());
		}
		else
		{
			writeErrorResponse(response, ServletError.UNKNOWN_ERROR);
		}
	}
}
