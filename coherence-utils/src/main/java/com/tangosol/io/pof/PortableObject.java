package com.tangosol.io.pof;

import java.io.IOException;

public interface PortableObject {
	 void readExternal(PofReader r) throws IOException;
	 void writeExternal(PofWriter w) throws IOException;
}
