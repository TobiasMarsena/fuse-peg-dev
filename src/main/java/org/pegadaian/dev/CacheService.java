package org.pegadaian.dev;

import org.apache.camel.Exchange;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("cacheService")
public class CacheService {
	
	ConfigurationBuilder cfg = InfinispanConfig.createConfig();
	Logger logger = LoggerFactory.getLogger(CacheService.class);
	String cacheName = "pegadaian-cache";
	
	public CacheService() {
	}
	
	public void createCache(RemoteCacheManager remote) {		
		remote.administration()
				.withFlags(CacheContainerAdmin.AdminFlag.PERMANENT)
				.getOrCreateCache(cacheName, "default");
		logger.info("Cache exist with name: {}", cacheName);
	}
	public void getCache(String key, Exchange exchange) {
		RemoteCacheManager remote = new RemoteCacheManager(cfg.build());
		final RemoteCache<String, String> remoteCache = remote.getCache(cacheName);
		String value = remoteCache.get(key);
		exchange.getIn().setBody(value, String.class);
	}
	public void putCache(String key, String value) {
		logger.info("Reached putCache Method");
		RemoteCacheManager remote = new RemoteCacheManager(cfg.build());
		logger.info("Instantiate remoteCacheManager");
		createCache(remote);
		logger.info("Created pegadaian-cache");
		final RemoteCache<String, String> remoteCache = remote.getCache(cacheName);
		logger.info("Get the pegadaian-cache");
		remoteCache.put(key, value);
		logger.info("Put a value to pegadaian-cache");
	}
}
