package org.pegadaian.dev;

import org.apache.camel.Exchange;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.springframework.stereotype.Service;

@Service("cacheService")
public class CacheService {
	
	ConfigurationBuilder cfg = InfinispanConfig.createConfig();
	
	public CacheService() {
	}
	
	public void createCache(RemoteCacheManager remote) {
		String cacheName = "pegadaian-cache";
		
		final RemoteCache<?,?> createdCache = remote.administration()
				.withFlags(CacheContainerAdmin.AdminFlag.PERMANENT)
				.getOrCreateCache(cacheName, "default");
		
		assert createdCache != null : "Expected created cache to be non-null";
	}
	public void getCache(String key, Exchange exchange) {
		RemoteCacheManager remote = new RemoteCacheManager(cfg.build());
		final RemoteCache<String, Object> remoteCache = remote.getCache("pegadaian-cache");
		Object value = remoteCache.get(key);
		exchange.getIn().setBody(value, String.class);
	}
	public void putCache(String key, Object value) {
		RemoteCacheManager remote = new RemoteCacheManager(cfg.build());
		createCache(remote);
		final RemoteCache<String,Object> remoteCache = remote.getCache("pegadaian-cache");
		remoteCache.put(key, value);
	}
	
}
