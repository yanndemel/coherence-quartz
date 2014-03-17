package org.scheduler.coherence;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SimpleJob implements IScheduledJob {

	@Override
	public void execute(String date, List<Map<String, String>> paramsList)
			throws JobExecutionException {
		Map<String, String> map = paramsList.get(0);
		
		System.out.println("Job "+map.get("id")+" executed at "+new Date()+" ("+map.get("info")+")");
		TestSimple.count++;
	}

}
