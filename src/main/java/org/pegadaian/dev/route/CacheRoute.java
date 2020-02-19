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
			.setHeader("cacheKey", simple("name"))
			.log("Sending >>>>> ${header.name} >>>>> to cache")
			.bean("cacheService", "putCache(${header.cacheKey}, ${header.name})")
		;
		from("direct:getCacheGreeting")
			.log("Retrieving data from cache")
			.setHeader("cacheKey", simple("name"))
			.bean("cacheService", "getCache(${header.cacheKey})")
			.log("Returned value: ${body}")
			.unmarshal().json(JsonLibrary.Jackson)
		;
	}
}
