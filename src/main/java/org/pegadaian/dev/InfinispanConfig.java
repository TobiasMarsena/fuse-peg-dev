package org.pegadaian.dev;

import java.util.Objects;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class InfinispanConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String service = "datagrid-app-hotrod";

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BasicCacheContainer remoteCacheContainer(Environment environment) {

        String serviceBaseName = service.toUpperCase().replace("-", "_");
        String host = environment.getProperty(serviceBaseName + "_SERVICE_HOST");
        String port = environment.getProperty(serviceBaseName + "_SERVICE_PORT");
        Objects.requireNonNull(host, "Infinispan service host not found in the environment");
        Objects.requireNonNull(port, "Infinispan service port not found in the environment");

        String hostPort = host + ":" + port;
        logger.info("Connecting to the Infinispan service at {}", hostPort);

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .forceReturnValues(true)
                .addServers(hostPort);

        return new RemoteCacheManager(builder.create(), false);
    }
    
}
