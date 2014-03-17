package org.scheduler.coherence;


public class SchedulerConstants {
	public static final long LOCK_TIMEOUT = 10L;
	
	private static SchedulerConstants instance = null;
		
	public static String SIMPLE_TRIGGERS_CACHE = "SimpleTriggers";
	public static String CRON_TRIGGERS_CACHE = "CronTriggers";
	
	public static final String MONITORING_CACHE = "Monitoring";
	public static final String INITIAL_LOAD_KEY = "InitialLoadKey";
	
	
	private String simpleCacheName;
	private String cronCacheName;
	
	
	
	private SchedulerConstants(String simpleCacheName, String cronCacheName) {
		this.simpleCacheName = simpleCacheName != null ? simpleCacheName : SIMPLE_TRIGGERS_CACHE;
		this.cronCacheName = cronCacheName != null ? cronCacheName : CRON_TRIGGERS_CACHE;
	}


	public static void createInstance() {
		createInstance(null, null);
	}
	
	public static void createInstance(String simpleCacheName, String cronCacheName) {
		instance = new SchedulerConstants(simpleCacheName, cronCacheName);
	}
	
	public static SchedulerConstants getInstance() {
		if(instance == null) {
			instance = new SchedulerConstants(null, null);
		}
		return instance;
	}


	public String getSimpleCacheName() {
		return simpleCacheName;
	}


	public String getCronCacheName() {
		return cronCacheName;
	}
	
}
