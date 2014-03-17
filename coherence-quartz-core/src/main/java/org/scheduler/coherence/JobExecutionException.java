package org.scheduler.coherence;

@SuppressWarnings("serial")
public class JobExecutionException extends Exception {

	public JobExecutionException() {
		super();
	}

	public JobExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobExecutionException(String message) {
		super(message);
	}

	public JobExecutionException(Throwable cause) {
		super(cause);
	}

	
	
}
