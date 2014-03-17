package org.scheduler.coherence.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scheduler.coherence.SchedulerConstants;
import org.scheduler.coherence.model.AbstractTrigger;
import org.scheduler.coherence.model.SimpleTrigger;
import org.scheduler.coherence.util.CacheFactory;

import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.filter.LessFilter;


public class CheckSimpleTrigggersTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(CheckSimpleTrigggersTask.class.getName());
	 
	private Timer timer;	
	private Map<String, SimpleTrigger> submittedTriggersById;
	private Map<String, SimpleTriggerExecutorTask> submittedTasksById;
	
	
	public CheckSimpleTrigggersTask(Timer timer) {
		super();
		this.timer = timer;
		this.submittedTriggersById = new HashMap<String, SimpleTrigger>();
		this.submittedTasksById = new HashMap<String, SimpleTriggerExecutorTask>();
		NamedCache cache = CacheFactory.getCache(SchedulerConstants.getInstance().getSimpleCacheName());
		cache.addMapListener(new MapListener() {
			
			@Override
			public void entryUpdated(MapEvent arg0) {}
			
			@Override
			public void entryInserted(MapEvent arg0) {}
			
			@Override
			public void entryDeleted(MapEvent e) {
				String id = (String) e.getKey();
				logger.info("Simple task "+id+" has been deleted => canceling task");
				SimpleTriggerExecutorTask task = submittedTasksById.get(id);
				if(task != null) {
					task.cancel();
				}
			}
		});
	}


	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		logger.log(Level.FINE, "CheckSimpleTrigggersTask");
		try {					
			NamedCache cache = CacheFactory.getCache(SchedulerConstants.getInstance().getSimpleCacheName());		
			PofExtractor lastExecExtractor = new PofExtractor(long.class, AbstractTrigger.LAST_EXEC);
			Filter f = new LessFilter(lastExecExtractor, new Date().getTime());
			Iterator<String> it = (Iterator<String>)cache.keySet(f).iterator();
			while(it.hasNext()) {
			    String id = (String) it.next();	
			    SimpleTrigger trigger = (SimpleTrigger) cache.get(id);
			    if(!submittedTriggersById.containsKey(id)) {		    	
					if(trigger != null && trigger.getLastExecTime() < new Date().getTime()) {
						if(cache.lock(id, SchedulerConstants.LOCK_TIMEOUT)) {						
							trigger = (SimpleTrigger) cache.get(id);
							if(trigger != null && trigger.getLastExecTime() < new Date().getTime()) {
								scheduleTask(id, trigger);	
							}
						}
					}						
				} else if(trigger.isRescheduled()){
					SimpleTriggerExecutorTask task = submittedTasksById.get(id);
					task.cancel();
					scheduleTask(id, trigger);	
					trigger.setRescheduled(false);
					cache.put(id, trigger);
				}
			}
			Iterator<String> iterator = submittedTriggersById.keySet().iterator();
			while(iterator.hasNext()) {
				String uid = iterator.next();
				SimpleTrigger trigger = submittedTriggersById.get(uid);
				if(trigger.getNbCurrFireTimes() == trigger.getNbMaxFireTimes()) {
					iterator.remove();
				}
			}
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Exception", t);			
		}			
	}


	private void scheduleTask(String id, SimpleTrigger trigger) {
		long startTime = trigger.getStartTime();
		SimpleTriggerExecutorTask task = new SimpleTriggerExecutorTask(SchedulerConstants.getInstance().getSimpleCacheName(), trigger);		
		if(startTime > 0) {
			if(trigger.getNbMaxFireTimes() == 1) {
				timer.schedule(task, new Date(startTime));
			} else {
				timer.scheduleAtFixedRate(task, new Date(startTime), trigger.getRepeatIntervall());
			} 
		} else {
			if(trigger.getNbMaxFireTimes() == 1) {
				timer.schedule(task, new Date(startTime));
			} else {
				timer.scheduleAtFixedRate(task, 0L, trigger.getRepeatIntervall());
			}
		}
		submittedTasksById.put(id, task);
		submittedTriggersById.put(id, trigger);
	}

}
