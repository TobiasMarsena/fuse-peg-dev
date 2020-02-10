package org.pegadaian.dev.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestOperationResponseMsgDefinition;
import org.apache.camel.model.rest.VerbDefinition;
import org.springframework.stereotype.Component;

@Component
public class WebHookRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		restConfiguration().apiContextPath("/openapi.json")
			.component("restlet").contextPath("/webhook").port("8080").scheme("http")
			.apiProperty("api.title", "Webhook 3Scale-SSO")
			.apiProperty("api.version", "1")
			.apiProperty("api.specification.contentType.json", "application/vnd.oai.openapi+json;version=2.0")
			.apiProperty("api.specification.contentType.yaml", "application/vnd.oai.openapi;version2.0")
		;
		
		rest().bindingMode(RestBindingMode.json).enableCORS(true).path("/sso").description("Greet Service")
			.post().id("3scale-sso-webhook").consumes("application/xml")
				.description("Content Based webhook to integrate 3Scale with RH-SSO")
				.responseMessage().code(200).message("Webhook successfully processed").endResponseMessage()
				.to("direct:webhook")
			;
		
		from("direct:webhook")
			.log("${body}")
		;
	}

	
}
