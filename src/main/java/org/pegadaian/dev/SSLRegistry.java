package org.pegadaian.dev;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

public class SSLRegistry {
	
	public SSLRegistry(CamelContext camelContext) {
		KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
        // Change this path to point to your truststore/keystore as jks files
        keyStoreParameters.setResource("ocpjakarta.jks");
        keyStoreParameters.setPassword("welcome2019");

        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
        keyManagersParameters.setKeyStore(keyStoreParameters);
        keyManagersParameters.setKeyPassword("welcome2019");

        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
        trustManagersParameters.setKeyStore(keyStoreParameters);

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(keyManagersParameters);
        sslContextParameters.setTrustManagers(trustManagersParameters);

        HttpComponent httpComponent = camelContext.getComponent("https4", HttpComponent.class);
        httpComponent.setSslContextParameters(sslContextParameters);
        //This is important to make your cert skip CN/Hostname checks
        httpComponent.setX509HostnameVerifier(new AllowAllHostnameVerifier());
	}
}
