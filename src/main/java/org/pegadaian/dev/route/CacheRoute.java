package org.pegadaian.dev.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.pegadaian.dev.CacheService;
import org.springframework.stereotype.Component;

@Component
public class CacheRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
				
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
			.log("Sending body >>>>> ${body} to cache")
			.bean(CacheService.class, "putCache(greeting, ${body})")
			.log("Successfully cache ${body} to Data Grid")
			.unmarshal().json(JsonLibrary.Jackson)
		;
		from("direct:getCacheGreeting")
			.log("Retrieving data from cache")
			.bean(CacheService.class, "getCache(greeting")
			.log("Returned value: ${body}")
			.unmarshal().json(JsonLibrary.Jackson)
		;
//		
//		from("direct:putCacheGreeting")
//			.id("greeting-to-cache")
//			.marshal().json(JsonLibrary.Jackson)
//			.convertBodyTo(String.class)
//			.setHeader("CamelInfinispanOperation", constant("CamelInfinispanOperationPut"))
//			.setHeader("CamelInfinispanValue", body())
//			.setHeader("CamelInfinispanKey", constant("Greeting"))
//			.setHeader("CamelInfinispanLifespanTime", constant("20s"))
//			.log("Sending body >>>>> ${body} >>>>> to cache")
//			.to("infinispan:cache-service?cacheContainer=#cacheManager&cacheName=pegadaian-cache")
//			.log("Success save to cache jboss data grid: ${body}")
//			.unmarshal().json(JsonLibrary.Jackson)
//		;
//		
//		from("direct:getCacheGreeting")
//			.setHeader("CamelInfinispanOperation", constant("CamelInfinispanOperationGet"))
//			.setHeader("CamelInfinispanKey", constant("Greeting"))
//			.to("infinispan:cache-service?cacheContainer=#cacheManager&cacheName=pegadaian-cache")
//			.setBody(header("CamelInfinispanOperationResult"))
//			.choice()
//				.when(header("CamelInfinispanOperationResult").isNotNull())
//					.convertBodyTo(String.class)
//					.unmarshal().json(JsonLibrary.Jackson)
//					.log("Get cache success >>> " + body())
//				.otherwise()
//					.log("Get nothing")
//			.endChoice()
//		;
	}

	
}
