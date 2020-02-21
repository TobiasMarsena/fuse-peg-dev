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
	String cacheName = "default";
	
	public CacheService() {
	}
	
	public RemoteCache<String, String> createCache(RemoteCacheManager remote) {	
		logger.info("Creating {}", cacheName);
		return remote.administration()
				.getOrCreateCache(cacheName, "default");
	}
	public void getCache(String key, Exchange exchange) {
		RemoteCacheManager remote = new RemoteCacheManager(cfg.build());
		final RemoteCache<String, String> remoteCache = remote.getCache(cacheName);
		String value = remoteCache.get(key);
		exchange.getIn().setBody(value, String.class);
		remote.close();
	}
	public void putCache(String key, String value) {
		logger.info("Reached putCache Method");
		RemoteCacheManager remote = new RemoteCacheManager(cfg.build());
		logger.info("Instantiate remoteCacheManager. isStarted returns {}", remote.isStarted());
//		RemoteCache<String, String> remoteCache = createCache(remote);
		RemoteCache<String, String> remoteCache = remote.getCache(cacheName);
		logger.info("Cache exist with name: {}", cacheName);
		remoteCache.put(key, value);
		logger.info("Put a value to pegadaian-cache");
	}
}
