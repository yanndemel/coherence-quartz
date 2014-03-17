package org.scheduler.coherence.model;

import java.io.IOException;
import java.util.Map;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

public class SimpleTrigger extends AbstractTrigger {

	private static final long serialVersionUID = 1L;
	
	public static final int REPEAT_INTERVAL = 6;
	public static final int START_TIME = 7;
	public static final int END_TIME = 8;
	private static final int RESCHEDULED = 9;

	// in milliseconds
	private int repeatIntervall;
	private long startTime;
	private long endTime;
	private boolean rescheduled;

	public SimpleTrigger() {
		super();
	}

	/**
	 * Constructs a new SimpleTrigger
	 * 
	 * @param uid UID of the trigger (must be unique)
	 * @param execClassName Qualified name of the java class to be launched when the trigger is fired : must implement {@link org.scheduler.coherence.IScheduledJob}
	 * @param nbMaxFireTimes Max number of times the scheduled job can be fired. Put zero if you don't want to set any limit.
	 * e.g. a job that must be every monday at 01:15 AM with a nbMaxFireTimes equals to 2 will be executed only the next two Mondays.
	 * It will be removed from the CronTriggers cache when the nbMaxFireTimes is reached. 
	 * @param nbCurrFireTimes Current count of executions of this job (default zero) : e.g. can be usefull for loading existing jobs into the CronTrigger cache
	 * @param lastExecTime Date of the last execution of the job (default zero)
	 * @param parameters Map of parameters that will be passed to the execute method of the job ({@link org.scheduler.coherence.IScheduledJob#execute})
	 * @param repeatIntervall Period time in milliseconds between successive job executions.
	 * @param startTime First time (in millis) at which job is to be executed.
	 * @param endTime Latter time beyond which the job does not execute. The trigger will be removed from the SimpleTriggers when the endTime is reached 
	 */
	public SimpleTrigger(String uid, String execClassName, int nbMaxFireTimes,
			int nbCurrFireTimes, long lastExecTime,
			Map<String, String> parameters, int repeatIntervall,
			long startTime, long endTime) {
		super(uid, execClassName, nbMaxFireTimes, nbCurrFireTimes,
				lastExecTime, parameters);
		this.repeatIntervall = repeatIntervall;
		this.startTime = startTime;
		this.endTime = endTime;
		this.rescheduled = false;
		checkTriggerValues();
	}

	private void checkTriggerValues() {
		if(nbMaxFireTimes <= 0) { // no max fire times defined
			int duration = (int) (endTime - startTime);
			if(duration > 0 && repeatIntervall > 0){
				// if the trigger has an end time, calculate max fire times
				nbMaxFireTimes = duration/repeatIntervall;
			}
			else if(nbMaxFireTimes == 0){
				/**
				 * no end time => put a negative max fire times to avoid task being removed immediately
				 * from CheckSimpleTrigggersTask.submittedTriggersById map if it starts in the future
				 */
				nbMaxFireTimes = -1;
			}
		}
	}

	//@UIAttribute(label = "End time", formatterClass = "com.hiperf.common.ui.client.format.LongToDateTimeFormatter")
	public long getEndTime() {
		return endTime;
	}

	//@UIAttribute(label = "Start time", formatterClass = "com.hiperf.common.ui.client.format.LongToDateTimeFormatter")
	public long getStartTime() {
		return startTime;
	}

	public int getRepeatIntervall() {
		return repeatIntervall;
	}

	public boolean isRescheduled() {
		return rescheduled;
	}

	public void setRescheduled(boolean rescheduled) {
		this.rescheduled = rescheduled;
	}

	@Override
	public void readExternal(PofReader r) throws IOException {
		super.readExternal(r);
		repeatIntervall = r.readInt(REPEAT_INTERVAL);
		startTime = r.readLong(START_TIME);
		endTime = r.readLong(END_TIME);
		rescheduled = r.readBoolean(RESCHEDULED);
	}

	@Override
	public void writeExternal(PofWriter w) throws IOException {
		super.writeExternal(w);
		w.writeInt(REPEAT_INTERVAL, repeatIntervall);
		w.writeLong(START_TIME, startTime);
		w.writeLong(END_TIME, endTime);
		w.writeBoolean(RESCHEDULED, rescheduled);
	}

}
