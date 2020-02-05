package org.pegadaian.dev;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class InfinispanConfig {
	
	private InfinispanConfig() {
	}
	
	public static ConfigurationBuilder createConfig() {
		ConfigurationBuilder cfg = new ConfigurationBuilder();
		
		cfg
			.addServer()
				.host("cache-service")
				.port(11222)
			.security().authentication()
				.enable()
				.username("pegadaian")
				.password("pegadaian")
				.serverName("cache-service")
			.ssl()
			.trustStorePath("/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt");
		
		return cfg;
	}

}
