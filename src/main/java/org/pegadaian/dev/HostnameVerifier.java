package org.pegadaian.dev;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;

public class HostnameVerifier extends AllowAllHostnameVerifier {
	
	public AllowAllHostnameVerifier getVerifier() {
		return new AllowAllHostnameVerifier();
        
	}
}
