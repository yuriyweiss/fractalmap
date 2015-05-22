package fractal.map.servlet.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import fractal.map.message.MessagesRegistrator;

public class ApplicationContextListener implements ServletContextListener
{
	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		ServletTransceiverClient.stopTransceiver();
	}

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		String logConfPath =
		    event.getServletContext().getRealPath("WEB-INF/conf/servlet-log4j.conf");
		PropertyConfigurator.configure(logConfPath);
		// !!! Dont't forget !!!
		MessagesRegistrator.registerMessages();
		ServletTransceiverClient.startTransceiver(event.getServletContext());
	}
}
