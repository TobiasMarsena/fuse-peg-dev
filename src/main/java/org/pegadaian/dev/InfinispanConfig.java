package org.pegadaian.dev;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class InfinispanConfig {
	ConfigurationBuilder cacheContainerConfig;
	
	public InfinispanConfig() {
		cacheContainerConfig = new ConfigurationBuilder();
		cacheContainerConfig.addServer()
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
		return new RemoteCacheManager(cacheContainerConfig.build());
	}

}
