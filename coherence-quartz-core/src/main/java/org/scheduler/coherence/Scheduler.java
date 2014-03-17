package org.scheduler.coherence;

import java.util.Timer;
import java.util.logging.Logger;

import org.scheduler.coherence.model.CronTrigger;
import org.scheduler.coherence.model.SimpleTrigger;
import org.scheduler.coherence.timer.CheckCronTrigggersTask;
import org.scheduler.coherence.timer.CheckSimpleTrigggersTask;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

public class Scheduler {
	
	private static final String QUARTZ_SCHEDULER = "quartz-scheduler";
	private static final int CHECK_PERIOD = 500;

	private static final Logger logger = Logger.getLogger(Scheduler.class.getName());
	
	private Timer[] timers;

	public Scheduler(String name, int nbTimers) {
		if(nbTimers <= 0)
			throw new RuntimeException("Timers nb must be > 0");
		this.timers = new Timer[nbTimers];
		String origName = name; 
		if(origName == null) {
			origName = QUARTZ_SCHEDULER;
		}
		int startDelay = CHECK_PERIOD / nbTimers;
		for(int i = 0 ; i< nbTimers ; i++) {			
			name = origName + "-" + i;			
			this.timers[i] = new Timer(name);
			this.timers[i].schedule(new CheckSimpleTrigggersTask(
					this.timers[i]),
					i*startDelay,
					CHECK_PERIOD);
            this.timers[i].schedule(new CheckCronTrigggersTask(this.timers[i]),
            		i*startDelay,
					CHECK_PERIOD);
            logger.info("Quartz-Scheduler "+name+" started");
		}
	}
	
	public void shutdown() {
		for(Timer t : timers) {
			t.cancel();
		}
	}
	
	public void addSimpleTrigger(SimpleTrigger trigger) {
		NamedCache cache = CacheFactory.getCache(SchedulerConstants.SIMPLE_TRIGGERS_CACHE);
		cache.put(trigger.getUid(), trigger);
	}
	
	public void addCronTrigger(CronTrigger trigger) {
		NamedCache cache = CacheFactory.getCache(SchedulerConstants.CRON_TRIGGERS_CACHE);
		cache.put(trigger.getUid(), trigger);
	}
	
	public void removeSimpleTrigger(SimpleTrigger trigger) {
		NamedCache cache = CacheFactory.getCache(SchedulerConstants.SIMPLE_TRIGGERS_CACHE);
		cache.remove(trigger.getUid());
	}
	
	public void removeCronTrigger(CronTrigger trigger) {
		NamedCache cache = CacheFactory.getCache(SchedulerConstants.CRON_TRIGGERS_CACHE);
		cache.remove(trigger.getUid());
	}

}
