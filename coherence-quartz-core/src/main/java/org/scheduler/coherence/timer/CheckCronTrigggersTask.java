package org.scheduler.coherence.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scheduler.coherence.SchedulerConstants;
import org.scheduler.coherence.model.AbstractTrigger;
import org.scheduler.coherence.model.CronTrigger;
import org.scheduler.coherence.util.CacheFactory;
import org.scheduler.coherence.util.DateHelper;

import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.extractor.AbstractExtractor;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.filter.AndFilter;
import com.tangosol.util.filter.ContainsFilter;
import com.tangosol.util.filter.LessEqualsFilter;
import com.tangosol.util.filter.LessFilter;
import com.tangosol.util.filter.OrFilter;

public class CheckCronTrigggersTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(CheckCronTrigggersTask.class.getName());
	
	private String cacheName;
	private Timer timer;
	
	public CheckCronTrigggersTask(Timer timer) {
		super();
		this.cacheName = SchedulerConstants.getInstance().getCronCacheName();
		this.timer = timer;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		logger.log(Level.FINE, "CheckCronTrigggersTask");
		try {
			NamedCache cache = CacheFactory.getCache(cacheName);
			Calendar cal = Calendar.getInstance();
			int curDay = cal.get(Calendar.DAY_OF_WEEK);
			int nbMinutesToday = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE); 
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long startDayTime = cal.getTimeInMillis();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			long prevDayStartTime = cal.getTimeInMillis();
			int prevDay = cal.get(Calendar.DAY_OF_WEEK);
			
			AbstractExtractor execDaysExtractor = new PofExtractor(List.class, CronTrigger.EXEC_DAYS);
			AbstractExtractor lastExecExtractor = new PofExtractor(long.class, AbstractTrigger.LAST_EXEC);
			AbstractExtractor execTimeExtractor = new PofExtractor(int.class, CronTrigger.EXEC_TIME);
			Filter f = new OrFilter(new AndFilter(new AndFilter(new LessFilter(lastExecExtractor, startDayTime), new ContainsFilter(execDaysExtractor, curDay)), new LessEqualsFilter(execTimeExtractor, nbMinutesToday)), 
					new AndFilter(new LessFilter(lastExecExtractor, prevDayStartTime), new ContainsFilter(execDaysExtractor, prevDay)));
			
			Map<String, List<CronTrigger>> todayTriggersByExecClass = new HashMap<String, List<CronTrigger>>();
			Map<String, List<CronTrigger>> yesterdayTriggersByExecClass = new HashMap<String, List<CronTrigger>>();
			Iterator<String> it = (Iterator<String>)cache.keySet(f).iterator();
				
			while(it.hasNext()) {
			    String id = (String) it.next();		 
			    if(cache.lock(id, SchedulerConstants.LOCK_TIMEOUT)) {
			    	boolean ok = false;
			    	CronTrigger trigger = (CronTrigger) cache.get(id);
			    	if(trigger != null) {
			    		if(trigger.getLastExecTime() != 0 && trigger.getLastExecTime() < prevDayStartTime && trigger.getExecDays().contains(prevDay)) {
			    			ok = true;
			    			addTriggerToMap(yesterdayTriggersByExecClass, trigger);
			    		} else if((trigger.getLastExecTime() == 0 || trigger.getLastExecTime() < startDayTime) && trigger.getExecDays().contains(curDay) && trigger.getExecTime() <= nbMinutesToday) {
			    			ok = true;
			    			addTriggerToMap(todayTriggersByExecClass, trigger);
			    		}
			    	}
			    	if(!ok) {
			    		cache.unlock(id);
			    	}
			    }			    
			}
			executeTriggers(cache, todayTriggersByExecClass, null);
			
			executeTriggers(cache, yesterdayTriggersByExecClass, DateHelper.formatYYYYmmDD(cal).toString());
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Exception", t);			
		}	
				
	}


	private void executeTriggers(NamedCache cache,
			Map<String, List<CronTrigger>> triggersByExecClass, String formattedLaunchDate) {
		for(String className : triggersByExecClass.keySet()) {
			List<CronTrigger> ctList = triggersByExecClass.get(className);
			List<String> uids = new ArrayList<String>(ctList.size());
				
			try {
				timer.schedule(new CronTriggerExecutorTask(cacheName, className, ctList, formattedLaunchDate), 0L);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Exception in CheckCronTrigggersTask", e);
				for(String uid : uids) {
					cache.unlock(uid);						
				}
			}
		}
	}


	private void addTriggerToMap(
			Map<String, List<CronTrigger>> triggersByExecClass,
			CronTrigger trigger) {
		String execClassName = trigger.getExecClassName();
		List<CronTrigger> ctList = triggersByExecClass.get(execClassName);
		if(ctList == null) {
			ctList = new ArrayList<CronTrigger>();
			triggersByExecClass.put(execClassName, ctList);
		}
		ctList.add(trigger);
	}


}
