package org.scheduler.coherence;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class InitializerServlet extends HttpServlet {
	
	private static final String THREADS_NB = "threadsNb";
	private static final String NAME = "name";
	
	private Scheduler scheduler;
	
	@Override
	public void init(ServletConfig config) throws ServletException {		
		
		super.init(config);		
		String name = config.getInitParameter(NAME);
		String nb = config.getInitParameter(THREADS_NB);
		if(nb != null) {
			scheduler = new Scheduler(name, Integer.valueOf(nb));
		} else {
			scheduler = new Scheduler(name, 1);
		}
		
	}

	@Override
	public void destroy() {
		super.destroy();
		scheduler.shutdown();
	}

	
	
}
