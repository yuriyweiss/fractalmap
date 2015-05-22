package fractal.map.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "change-layer", urlPatterns = { "/change-layer" })
public class ChangeLayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ChangeLayerServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// return if session does not exist, form must be initialized
		if (session.isNew()) return;
		
		Double re = Double.parseDouble(request.getParameter(Names.RE));
		session.setAttribute(Names.CENTER_RE, re);
		Double im = Double.parseDouble(request.getParameter(Names.IM));
		session.setAttribute(Names.CENTER_IM, im);
		Integer layerIndex = Integer.parseInt(request.getParameter(Names.LAYER_INDEX));
		session.setAttribute(Names.CURRENT_LAYER_INDEX, layerIndex);
	}
}
