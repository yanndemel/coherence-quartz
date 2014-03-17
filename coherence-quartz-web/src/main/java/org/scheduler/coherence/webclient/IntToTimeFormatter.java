package org.scheduler.coherence.webclient;

import com.hiperf.common.ui.client.IFormatter;
import com.hiperf.common.ui.client.exception.ParseException;

public class IntToTimeFormatter implements IFormatter<Integer> {

	public String format(Integer n) {
		int h = n / 60;
		int m = n % 60;
		StringBuilder sb = new StringBuilder();
		if(h < 10)
			sb.append("0");
		sb.append(h).append(":");
		if(m < 10)
			sb.append("0");
		sb.append(m);
		return sb.toString();
	}

	public Integer parse(String s) throws ParseException {
		int i = s.indexOf(":");
		if(i != 2 || s.length() != 5)
			throw new ParseException("Bad format : enter time format hh:mm");
		String mm = s.substring(3);
		int r = 0;
		if(mm.startsWith("0"))
			r = Integer.parseInt(mm.substring(1));
		else
			r = Integer.parseInt(mm);
		r += Integer.parseInt(s.substring(0,2));
		return r;
	}
	

}
