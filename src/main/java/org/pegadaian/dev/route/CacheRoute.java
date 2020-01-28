package org.pegadaian.dev.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class CacheRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		onException(Exception.class)
			.handled(true)
			.maximumRedeliveries(2)
		;
	}
}
