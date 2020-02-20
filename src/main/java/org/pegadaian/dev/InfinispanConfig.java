package org.pegadaian.dev;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;

public class InfinispanConfig {
	
	private InfinispanConfig() {
	}
	
	public static ConfigurationBuilder createConfig() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.addServer()
			.host("cache-service").port(11222)
			.security()
		        .authentication().enable()
		        .username("pegadaian")
		        .password("pegadaian")
		        .serverName("cache-service")
		        .saslQop(SaslQop.AUTH)
		        .ssl()
		        .trustStorePath("/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt");
		return builder;
	}

}
