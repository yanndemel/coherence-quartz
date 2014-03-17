package org.scheduler.coherence;

import java.util.List;
import java.util.Map;

public interface IScheduledJob {
	
	void execute(String formattedDate, List<Map<String, String>> paramsList) throws JobExecutionException;
}
