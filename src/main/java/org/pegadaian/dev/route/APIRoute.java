package org.pegadaian.dev.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.pegadaian.dev.Greeting;
import org.springframework.stereotype.Component;

@Component
public class APIRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		rest().bindingMode(RestBindingMode.json).enableCORS(true).path("/hello").description("Greet Service")
		.get("/{name}").id("list").outType(Greeting.class).produces("application/json")
			.description("Greet user that use this service")
			.responseMessage().code(200).message("All users successfully returned").endResponseMessage()
			.to("bean:greetService?method=greetUser")
		;
	}

	
}
