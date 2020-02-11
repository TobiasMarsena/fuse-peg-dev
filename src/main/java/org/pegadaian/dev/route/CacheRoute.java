package org.pegadaian.dev.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestOperationResponseMsgDefinition;
import org.apache.camel.model.rest.VerbDefinition;
import org.pegadaian.dev.CacheService;
import org.pegadaian.dev.Greeting;
import org.springframework.stereotype.Component;

@Component
public class CacheRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
				
		onException(Exception.class)
			.handled(true)
			.maximumRedeliveries(2)
		;
		
		rest().bindingMode(RestBindingMode.json).enableCORS(true).path("/hello").description("Greet Service")
			.get("/{name}").id("list").outType(Greeting.class).produces("application/json")
				.description("Greet user that use this service")
				.responseMessage().code(200).message("All users successfully returned").endResponseMessage()
				.to("bean:greetService?method=greetUser")
			.get("/list/cache").id("cache").outType(Greeting.class).produces("application/json")
				.description("Return all cache")
				.responseMessage().code(200).message("All cache successfully returned").endResponseMessage()
				.to("direct:getCacheGreeting")
			.get("/cache/{name}").id("putCache").consumes("application/json")
				.description("Put cache data")
				.responseMessage().code(200).message("Data successfully cached").endResponseMessage()
				.to("direct:putCache")
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
			.bean("cacheService", "putCache(greeting, ${body})")
			.log("Successfully cache ${body} to Data Grid")
			.unmarshal().json(JsonLibrary.Jackson)
		;
		from("direct:getCacheGreeting")
			.log("Retrieving data from cache")
			.bean("cacheService", "getCache(greeting")
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
