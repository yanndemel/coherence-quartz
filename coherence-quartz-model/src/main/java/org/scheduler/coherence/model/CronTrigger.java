package org.scheduler.coherence.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gwtgen.api.shared.UIAttribute;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

public class CronTrigger extends AbstractTrigger {

	private static final long serialVersionUID = 1L;
	
	public static final int EXEC_DAYS = 6;
	public static final int EXEC_TIME = 7;

	private List<Integer> execDays;
	// Nb of minutes from 00:00
	private int execTime;

	public CronTrigger() {
		super();
	}

	/**
	 * Constructs a new CronTrigger (unix crontab like trigger) with following arguments :
	 * 
	 * @param uid UID of the trigger (must be unique)
	 * @param execClassName Qualified name of the java class to be launched when the trigger is fired : must implement {@link org.scheduler.coherence.IScheduledJob}
	 * @param nbMaxFireTimes Max number of times the scheduled job can be fired. Put zero if you don't want to set any limit.
	 * e.g. a job that must be every monday at 01:15 AM with a nbMaxFireTimes equals to 2 will be executed only the next two Mondays.
	 * It will be removed from the CronTriggers cache when the nbMaxFireTimes is reached. 
	 * @param nbCurrFireTimes Current count of executions of this job (default zero) : e.g. can be usefull for loading existing jobs into the CronTrigger cache
	 * @param lastExecTime Date of the last execution of the job (default zero)
	 * @param parameters Map of parameters that will be passed to the execute method of the job ({@link org.scheduler.coherence.IScheduledJob#execute})
	 * @param execDays List of days when the job must be executed (list of {@link java.util.Calendar.MONDAY}, {@link java.util.Calendar.TUESDAY}, ...)
	 * @param execTime Time when the job has to be executed : number of minutes since midnight 
	 */
	public CronTrigger(String uid, String execClassName, int nbMaxFireTimes,
			int nbCurrFireTimes, long lastExecTime,
			Map<String, String> parameters, List<Integer> execDays, int execTime) {
		super(uid, execClassName, nbMaxFireTimes, nbCurrFireTimes,
				lastExecTime, parameters);
		this.execDays = execDays;
		this.execTime = execTime;
	}

	public List<Integer> getExecDays() {
		return execDays;
	}

	@UIAttribute(formatterClass = "org.scheduler.coherence.webclient.IntToTimeFormatter")
	public int getExecTime() {
		return execTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(PofReader r) throws IOException {
		super.readExternal(r);
		execDays = (List<Integer>) r.readCollection(EXEC_DAYS,
				new ArrayList<Integer>());
		execTime = r.readInt(EXEC_TIME);
	}

	@Override
	public void writeExternal(PofWriter w) throws IOException {
		super.writeExternal(w);
		w.writeCollection(EXEC_DAYS, execDays, Integer.class);
		w.writeInt(EXEC_TIME, execTime);
	}

}
