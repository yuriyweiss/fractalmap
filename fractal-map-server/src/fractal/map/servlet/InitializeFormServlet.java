package fractal.map.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import fractal.map.calc.Constants;
import fractal.map.model.Layer;
import fractal.map.model.LayerRegistry;
import fractal.map.servlet.response.InitializeFormInfo;
import fractal.map.servlet.response.LayerInfo;

@WebServlet(name = "initialize-form", urlPatterns = { "/initialize-form" })
public class InitializeFormServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public InitializeFormServlet()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute(Names.CENTER_RE) == null)
		{
			Double centerRe = new Double((Constants.RE_RIGHT + Constants.RE_LEFT) / 2);
			Double centerIm = new Double((Constants.IM_TOP + Constants.IM_BOTTOM) / 2);
			session.setAttribute(Names.CENTER_RE, centerRe);
			session.setAttribute(Names.CENTER_IM, centerIm);
			session.setAttribute(Names.CURRENT_LAYER_INDEX, new Integer(1));
		}
		Double centerRe = (Double) session.getAttribute(Names.CENTER_RE);
		Double centerIm = (Double) session.getAttribute(Names.CENTER_IM);
		Integer currentLayerIndex = (Integer) session.getAttribute(Names.CURRENT_LAYER_INDEX);
		InitializeFormInfo initializeFormInfo =
		    new InitializeFormInfo(centerRe, centerIm, currentLayerIndex);
		for (Layer layer : LayerRegistry.getLayers())
		{
			initializeFormInfo.addLayer(new LayerInfo(layer.getLayerIndex()));
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new Gson().toJson(initializeFormInfo));
	}
}
