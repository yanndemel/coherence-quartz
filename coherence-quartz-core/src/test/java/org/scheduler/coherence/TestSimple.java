package org.scheduler.coherence;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.littlegrid.ClusterMemberGroup;
import org.littlegrid.ClusterMemberGroupUtils;
import org.scheduler.coherence.model.CronTrigger;
import org.scheduler.coherence.model.SimpleTrigger;

import com.tangosol.util.UUID;


public class TestSimple {

	
	static int count; 
	private static ClusterMemberGroup memberGroup;

    @BeforeClass
    public static void beforeTests() {
        memberGroup = ClusterMemberGroupUtils.newBuilder()
        		.setStorageEnabledCount(1)
        		.setFastStartJoinTimeoutMilliseconds(100) 
                .setCacheConfiguration("coherence-quartz-cache-config.xml")
                .buildAndConfigureForStorageDisabledClient();
        scheduler = new Scheduler("test-scheduler", 3);
    }

    @AfterClass
    public static void afterTests() {
    	scheduler.shutdown();
        ClusterMemberGroupUtils.shutdownCacheFactoryThenClusterMemberGroups(memberGroup);
    }

	private static Scheduler scheduler;

	
	@Test
	public void testSimple() throws InterruptedException {
		Map<String,String> map = new HashMap<String, String>();
		
		String id = new UUID().toString();
		map.put("id", id);
		map.put("info", "simple job repeats infinitely");
		SimpleTrigger st = new SimpleTrigger(id, SimpleJob.class.getName(), 0, 0,0L, map, 500, 0L, 0L);   
		scheduler.addSimpleTrigger(st);
		do {
			Thread.sleep(500L);
			System.out.println("Count = "+count);
				
		} while(count < 10);
		assertEquals(10, count);
		scheduler.removeSimpleTrigger(st);
		count = 0;
	}
	
	@Test
	public void testCron() throws InterruptedException {
		Map<String,String> map = new HashMap<String, String>();
		
		String id = new UUID().toString();
		map.put("id", id);
		map.put("info", "Cron job repeats once");
		List<Integer> l = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		l.add(cal.get(Calendar.DAY_OF_WEEK));
		int time = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		CronTrigger st = new CronTrigger(id, SimpleJob.class.getName(), 0, 0,0L, map, l, time);   
		scheduler.addCronTrigger(st);
		Thread.sleep(1000L);
		System.out.println("Count = "+count);
		assertEquals(1, count);
		count = 0;
	}
	
	
	@Test
	public void testSimpleNoRepeat() throws InterruptedException {
		Map<String,String> map = new HashMap<String, String>();
		
		String id = new UUID().toString();
		map.put("id", id);
		map.put("info", "Simple job repeats 4");
		int max = 4;
		SimpleTrigger st = new SimpleTrigger(id, SimpleJob.class.getName(), max, 0,0L, map, 500, 0L, 0L);   
		scheduler.addSimpleTrigger(st);
		int c = 0;
		do {
			Thread.sleep(500L);
			System.out.println("Count = "+count);
			c++;
		} while(c < 10);
		assertEquals(max, count);
		scheduler.removeSimpleTrigger(st);
		count = 0;
	}
	
}
