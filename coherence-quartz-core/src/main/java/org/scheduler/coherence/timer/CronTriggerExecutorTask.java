package org.scheduler.coherence.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scheduler.coherence.IScheduledJob;
import org.scheduler.coherence.JobExecutionException;
import org.scheduler.coherence.model.CronTrigger;
import org.scheduler.coherence.util.CacheFactory;

import com.tangosol.net.NamedCache;

public class CronTriggerExecutorTask extends TimerTask {

	private static final Logger logger = Logger
			.getLogger(CronTriggerExecutorTask.class.getName());

	private String cacheName;
	private List<CronTrigger> triggers;
	private IScheduledJob job;
	private String date;

	public CronTriggerExecutorTask(String cacheName,
			String className, List<CronTrigger> triggers,
			String formattedLaunchDate) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		this.cacheName = cacheName;
		this.triggers = triggers;
		this.job = (IScheduledJob) Class.forName(className).newInstance();
		this.date = formattedLaunchDate;
	}

	@Override
	public void run() {	
		try {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (CronTrigger ct : triggers) {
				mapList.add(ct.getParameters());
			}
			try {
				job.execute(date, mapList);
			} catch (JobExecutionException e) {
				logger.log(Level.SEVERE, "Execution exception...", e);
			}
			NamedCache cache = CacheFactory.getCache(cacheName);
			long time = new Date().getTime();
			for (CronTrigger ct : triggers) {
				ct.setLastExecTime(time);
				ct.increaseNbCurrFireTimes();
				String uid = ct.getUid();
				if (ct.getNbCurrFireTimes() == ct.getNbMaxFireTimes()) {
					cache.remove(uid);
				} else {
					cache.put(uid, ct);
				}
	
				/*if(date == null) {
					checkCronTrigggersTask.removeFromTodayList(uid);
				} else {
					checkCronTrigggersTask.removeFromYesterdayList(uid);
				}*/
				
				cache.unlock(uid);
			}
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Exception", t);			
		}

	}

}
