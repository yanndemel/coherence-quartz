package org.scheduler.coherence.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.scheduler.coherence.SchedulerConstants;

import com.tangosol.net.NamedCache;

public class CacheFactory {

	private static final Logger logger = Logger.getLogger(CacheFactory.class.getName());
	
	public static NamedCache getCache(String cacheName) {
		return getCache(cacheName, true);
	}
	
	public static NamedCache getCache(String cacheName, boolean pause) {
		try {
			return com.tangosol.net.CacheFactory.getCache(cacheName);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while trying to access the cache "+cacheName, e);
			if(pause) {				
				try {
					return sleepAndRetry(cacheName);
				} catch (InterruptedException e1) {
					logger.log(Level.SEVERE, "InterruptedException", e1);
					return null;
				}
			} else
				return null;
		}		
	}

	private static NamedCache sleepAndRetry(String cacheName)
			throws InterruptedException {
		Thread.sleep(5000L);
		try {
			NamedCache cache = getCache(SchedulerConstants.MONITORING_CACHE, false);						
			if(cache != null && cache.containsKey(SchedulerConstants.INITIAL_LOAD_KEY)) {
				return com.tangosol.net.CacheFactory.getCache(cacheName);
			} else {
				return sleepAndRetry(cacheName);
			}
		} catch (Exception e) {
			return sleepAndRetry(cacheName);
		}
	}

}
