package org.pegadaian.dev.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.stereotype.Component;

@Component
public class CacheRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		Configuration cacheContainerConfig = new ConfigurationBuilder()
				.addServer()
					.host("cache-service")
					.port(11222)
					.security()
						.authentication().enable()
						.username("pegadaian")
						.password("pegadaian")
						.serverName("cache-service")
				.build();
		
		onException(Exception.class)
			.handled(true)
			.maximumRedeliveries(2)
		;
		
		from("direct:putCache")
			.id("put-cache")
			.bean("greetService", "greetUser")
			.to("direct:putCacheGreeting")
		;
		
		from("direct:putCacheGreeting")
			.id("greeting-to-cache")
			.marshal().json(JsonLibrary.Jackson)
			.convertBodyTo(String.class)
			.setHeader("CamelInfinispanOperation", constant("CamelInfinispanOperationPut"))
			.setHeader("CamelInfinispanValue", body())
			.setHeader("CamelInfinispanKey", constant("Greeting"))
			.setHeader("CamelInfinispanLifespanTime", constant("20s"))
			.log("Sending body >>>>> ${body} >>>>> to cache")
			.to("infinispan:pegadaian-cache?cacheContainer=#cacheContainerConfig")
			.log("Success save to cache jboss data grid: ${body}")
			.unmarshal().json(JsonLibrary.Jackson)
		;
		
		from("direct:getCacheGreeting")
			.setHeader("CamelInfinispanOperation", constant("CamelInfinispanOperationGet"))
			.setHeader("CamelInfinispanKey", constant("Greeting"))
			.to("infinispan:pegadaian-cache?cacheContainer=#cacheContainerConfig")
			.setBody(header("CamelInfinispanOperationResult"))
			.choice()
				.when(header("CamelInfinispanOperationResult").isNotNull())
					.convertBodyTo(String.class)
					.unmarshal().json(JsonLibrary.Jackson)
					.log("Get cache success >>> " + body())
				.otherwise()
					.log("Get nothing")
			.endChoice()
		;
	}

	
}
