package org.pegadaian.dev.route;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.pegadaian.dev.Client;
import org.pegadaian.dev.ClientService;
import org.pegadaian.dev.Event;
import org.pegadaian.dev.Token;
import org.springframework.stereotype.Component;

@Component
public class WebHookRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
		JaxbDataFormat eventDataFormat = new JaxbDataFormat(jaxbContext);
		
		restConfiguration().apiContextPath("/openapi.json")
			.component("restlet").contextPath("/webhook").port("8080").scheme("http")
			.apiProperty("api.title", "Webhook 3Scale-SSO")
			.apiProperty("api.version", "1")
			.apiProperty("api.specification.contentType.json", "application/vnd.oai.openapi+json;version=2.0")
			.apiProperty("api.specification.contentType.yaml", "application/vnd.oai.openapi;version2.0")
		;
		
		rest().path("/sso")
			.post().id("3scale-sso-webhook").consumes("application/xml")
				.description("Content Based webhook to integrate 3Scale with RH-SSO")
				.responseMessage().code(200).message("Webhook successfully processed").endResponseMessage()
				.to("direct:appCreated")
			;
		
		from("timer:initialAccessToken?repeatCount=1")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setBody(simple("grant_type=password&username={{sso.username}}&password={{sso.password}}&client_id=admin-cli"))
			
			.to("https4://{{sso.host.ocp-jkt}}/auth/realms/master/protocol/openid-connect/token")
			.unmarshal().json(JsonLibrary.Gson, Token.class)
			.bean("token", "setAccess_token(${body.getAccess_token})")
			.bean("token", "setRefresh_token(${body.getRefresh_token})")
			.log("Token to access Single Sign-On is saved. Automatically refresh the token when expiry is due.")
		;
		
		from("timer:refreshToken?delay=60s&period=60s")
			.setHeader("Authorization", method("token", "bearerAuth"))
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setBody(method("token", "refreshBodyRequest"))

			.to("https4://{{sso.host.ocp-jkt}}/auth/realms/master/protocol/openid-connect/token")			
			.unmarshal().json(JsonLibrary.Gson, Token.class)
			.bean("token", "setAccess_token(${body.getAccess_token})")
			.bean("token", "setRefresh_token(${body.getRefresh_token})")
			.log("Successfully request new refreshed token.")
		;
		
		from("direct:appCreated")
			.unmarshal(eventDataFormat)
			.log("Received webhook from 3Scale. Processing . . .")
			.removeHeaders("Camel*")
			.setHeader("Authorization", method("token", "bearerAuth"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("clientId=${body.getApplicationId}"))
			.bean("clientService", "sleep")
			
			.to("https4://{{sso.host.ocp-jkt}}/auth/admin/realms/3scale-sso/clients")
			.unmarshal().json(JsonLibrary.Gson, Client[].class)
		
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("PUT"))
			.setHeader(Exchange.HTTP_PATH, simple("${body[0].getId}"))
			.setBody(method("clientService", "changeFlow"))
			.log("Sending PUT Request with ${body}")
			.marshal().json(JsonLibrary.Gson, Client.class)
			.to("https4://{{sso.host.ocp-jkt}}/auth/admin/realms/3scale-sso/clients/")
			.log("Successfully change the Auth flow to Direct Access Grants with status code: ${header.CamelHttpResponseCode}")
		;
	}

	
}
