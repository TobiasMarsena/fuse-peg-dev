package org.pegadaian.dev;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class InfinispanConfig {
	ConfigurationBuilder clientBuilder;
	
	public InfinispanConfig() {
		clientBuilder = new ConfigurationBuilder();
		clientBuilder.addServer()
			.host("cache-service")
			.port(11222)
			.security()
				.authentication().enable()
				.username("pegadaian")
				.password("pegadaian")
				.serverName("cache-service")
		.build();
	}
	
	public RemoteCacheManager newCacheManager() {
		return new RemoteCacheManager(clientBuilder.build());
	}

}
