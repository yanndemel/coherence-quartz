package org.scheduler.coherence.model;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.gwtgen.api.shared.INakedObject;
import org.gwtgen.api.shared.UIAttribute;

//import com.hiperf.common.ui.client.INakedObject;
//import com.hiperf.common.ui.client.annotation.UIAttribute;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

@Entity
public abstract class AbstractTrigger implements PortableObject, INakedObject 
{

	private static final long serialVersionUID = 1L;
	
	private static final int UID = 0;
	private static final int EXEC_CLASS_NAME = 1;
	private static final int NB_MAX = 2;
	private static final int NB_CUR = 3;
	public static final int LAST_EXEC = 4;
	private static final int PARAMS = 5;

	private String uid;
	private String execClassName;
	protected int nbMaxFireTimes;
	private int nbCurrFireTimes;
	// in milliseconds : date from 1970
	private long lastExecTime;
	private Map<String, String> parameters;

	public AbstractTrigger() {
		super();
	}

	@Id
	@GeneratedValue
	public String getUid() {
		return uid;
	}

	public String getExecClassName() {
		return execClassName;
	}

	public int getNbMaxFireTimes() {
		return nbMaxFireTimes;
	}

	public int getNbCurrFireTimes() {
		return nbCurrFireTimes;
	}

	@UIAttribute(formatterClass = "com.hiperf.common.ui.client.format.LongToDateTimeFormatter")
	public long getLastExecTime() {
		return lastExecTime;
	}
	
	public void increaseNbCurrFireTimes() {
		if(this.nbCurrFireTimes < Integer.MAX_VALUE )
			this.nbCurrFireTimes++;
	}

	public void setNbCurrFireTimes(int nbCurrFireTimes) {
		this.nbCurrFireTimes = nbCurrFireTimes;
	}

	public void setLastExecTime(long lastExecTime) {
		this.lastExecTime = lastExecTime;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public AbstractTrigger(String uid, String execClassName,
			int nbMaxFireTimes, int nbCurrFireTimes, long lastExecTime,
			Map<String, String> parameters) {
		super();
		this.uid = uid;
		this.execClassName = execClassName;
		this.nbMaxFireTimes = nbMaxFireTimes;
		this.nbCurrFireTimes = nbCurrFireTimes;
		this.lastExecTime = lastExecTime;
		this.parameters = parameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(PofReader r) throws IOException {
		uid = r.readString(UID);
		execClassName = r.readString(EXEC_CLASS_NAME);
		nbMaxFireTimes = r.readInt(NB_MAX);
		nbCurrFireTimes = r.readInt(NB_CUR);
		lastExecTime = r.readLong(LAST_EXEC);
		parameters = (Map<String, String>) r.readMap(PARAMS,
				new HashMap<String, Object>());
	}

	@Override
	public void writeExternal(PofWriter w) throws IOException {
		w.writeString(UID, uid);
		w.writeString(EXEC_CLASS_NAME, execClassName);
		w.writeInt(NB_MAX, nbMaxFireTimes);
		w.writeInt(NB_CUR, nbCurrFireTimes);
		w.writeLong(LAST_EXEC, lastExecTime);
		w.writeMap(PARAMS, parameters, String.class, String.class);
	}

}
