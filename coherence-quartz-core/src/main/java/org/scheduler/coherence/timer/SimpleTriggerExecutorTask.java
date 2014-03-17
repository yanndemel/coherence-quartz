package org.scheduler.coherence.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scheduler.coherence.IScheduledJob;
import org.scheduler.coherence.model.SimpleTrigger;
import org.scheduler.coherence.util.CacheFactory;

import com.tangosol.net.NamedCache;

public class SimpleTriggerExecutorTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(SimpleTriggerExecutorTask.class.getName());
	
	private SimpleTrigger trigger;
	private String cacheName;
	
	
	public SimpleTriggerExecutorTask(String cacheName, SimpleTrigger trigger) {
		super();
		this.trigger = trigger;
		this.cacheName = cacheName;
	}


	@Override
	public void run() {
		try {	
			NamedCache cache = CacheFactory.getCache(cacheName);
			String uid = trigger.getUid();
			if(trigger.getEndTime() > 0 && trigger.getEndTime() < System.currentTimeMillis()) {
				cancelTask(uid);		
			} else {
				IScheduledJob job = (IScheduledJob)Class.forName(trigger.getExecClassName()).newInstance();
				List<Map<String, String>> paramsList = new ArrayList<Map<String,String>>(1);
				paramsList.add(trigger.getParameters());
				job.execute(null, paramsList);
				trigger.setLastExecTime(new Date().getTime());
				trigger.increaseNbCurrFireTimes();
				if(trigger.getNbCurrFireTimes() == trigger.getNbMaxFireTimes()) {
					cancelTask(uid);								
				} else {
					cache.put(uid, trigger);
				}				
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in SimpleTriggerExecutorTask", e);
		}
		
		
	}


	private void cancelTask(String uid) {
		try {
			NamedCache cache = CacheFactory.getCache(cacheName);
			cache.remove(uid);
			cache.unlock(uid);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in SimpleTriggerExecutorTask", e);
		}
		super.cancel();
	}

}
