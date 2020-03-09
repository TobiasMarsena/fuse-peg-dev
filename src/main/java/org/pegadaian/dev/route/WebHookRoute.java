package org.pegadaian.dev.route;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.pegadaian.dev.model.Event;
import org.pegadaian.dev.process.FindPlan;
import org.pegadaian.dev.process.FindService;
import org.springframework.stereotype.Component;

@Component
public class WebHookRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		Processor findService = new FindService();
		Processor findPlan = new FindPlan();
		JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
		JaxbDataFormat eventDataFormat = new JaxbDataFormat(jaxbContext);
		
		restConfiguration().apiContextPath("/openapi.json")
			.component("restlet").contextPath("/webhook").port("8080").scheme("http")
			.apiProperty("api.title", "Webhook 3Scale-SSO")
			.apiProperty("api.version", "1")
			.apiProperty("api.specification.contentType.json", "application/vnd.oai.openapi+json;version=2.0")
			.apiProperty("api.specification.contentType.yaml", "application/vnd.oai.openapi;version2.0")
		;
		
		rest().path("/")
			.post().id("3scale-webhook").consumes("application/xml")
				.description("Content Based webhook to integrate 3Scale JKT to SBY")
				.responseMessage().code(200).message("Webhook successfully processed").endResponseMessage()
				.to("direct:webhookType")
			;
		
		from("timer:test?repeatCount=1")
			.log("Testing SSL to 3Scale JKT")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.source.api}}"))
			.to("{{threescale.source.url}}/admin/api/accounts.xml")
			.log("${headers}")
			.log("${body}")
			
			.log("Testing SSL to 3Scale SBY")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.to("{{threescale.dest.url}}/admin/api/accounts.xml")
			.log("${headers}")
			.log("${body}")
		;
		
		from("direct:webhookType").id("Content Based Router")
			.unmarshal(eventDataFormat)
			.choice()
				.when().simple("${body.getType} == 'account' && ${body.getAction} == 'created'")
					.to("direct:accountCreated")
				.when().simple("${body.getType} == 'application' && ${body.getAction} == 'created'")
					.to("direct:appCreated")
				.when().simple("${body.getType} == 'application' && ${body.getAction} == 'key_updated'")
					.to("direct:keyUpdated")
			.endChoice()
		;
		
		from("direct:keyUpdated")
			.setHeader("new_application_key", simple("${body.getObject.getApplication.getKeys.getKeys.get(0)}"))
//			Delete current key
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "app_id=${body.getObject.getApplication.getApp_id}"))
			.to("{{threescale.dest.url}}/admin/api/applications/find.xml")
			.unmarshal(eventDataFormat)
			
			.setHeader("AccountID", simple("${body.getAccount_id}"))
			.setHeader("AppID", simple("${body.getId}"))
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("{{threescale.dest.url}}/admin/api/accounts/"
					+ "${body.getAccount_id}/applications/"
					+ "${body.getId}/keys/"
					+ "${body.getKeys.getKeys.get(0)}.xml")
			.log("Old key deleted. Creating new synced key . . .")
			
//			Create new synced key
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "key=${header.new_application_key}"))
			.toD("{{threescale.dest.url}}/admin/api/accounts/"
					+ "${header.AccountID}/applications/"
					+ "${header.AppID}/keys.xml")
			.log("New Key updated and synced")
		;
		
		from("direct:accountCreated").id("Account Created Router")
//			Create Account
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "org_name=${body.getOrg_name}&"
					+ "username=${body.getUsername}&"
					+ "email=${body.getEmail}"))
			.to("{{threescale.dest.url}}/admin/api/signup.xml")
			.unmarshal(eventDataFormat)

//			Approve Account
			.log("Account has been created with Organization Name ${body.getOrg_name}")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("PUT"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("{{threescale.dest.url}}/admin/api/accounts/"
					+ "${body.getId}/approve.xml")
			.unmarshal(eventDataFormat)

//			Activate User
			.log("Account approved.")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("PUT"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("{{threescale.dest.url}}/admin/api/accounts/"
					+ "${body.getId}/users/"
					+ "${body.getUserId}/activate.xml")
			.log("Account user activated.")
		;
		
		
		from("direct:appCreated").id("Application Created Router")
//			Save some value for future query and App creation
			.setHeader("ServiceID", simple("${body.getObject.getApplication.getService_id}"))
			.setHeader("PlanName", simple("${body.getObject.getApplication.getPlan.getName}"))
			.setHeader("name", simple("${body.getObject.getApplication.getName}"))
			.setHeader("description", simple("${body.getObject.getApplication.getDescription}"))
			.setHeader("application_id", simple("body.getObject.getApplication.getApp_id"))
			.setHeader("application_key", simple("${body.getObject.getApplication.getKeys.getKeys.get(0)}"))
//			Get Account
			.log("Retrieving additional info for application creation . . .")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.source.api}}"))
			.toD("{{threescale.source.url}}/admin/api/accounts/"
					+ "${body.getObject.getApplication.getAccount_id}.xml")
			.unmarshal(eventDataFormat)
			
//			Find Account id
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "username=${body.getUsers().get(0).getUsername()}&"
					+ "email=${body.getUsers().get(0).getEmail()}"))
			.to("{{threescale.dest.url}}/admin/api/accounts/find.xml")
			.unmarshal(eventDataFormat)
//			Remember Account ID
			.setHeader("AccountID", simple("${body.getId}"))
			
//			Get Service
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.source.api}}"))
			.toD("{{threescale.source.url}}/admin/api/services/"
					+ "${header.ServiceID}.xml")
			.unmarshal(eventDataFormat)
			.setHeader("ServiceName", simple("${body.getSystem_name}"))
			
//			Find Service ID
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.to("{{threescale.dest.url}}/admin/api/services.xml")
			.unmarshal(eventDataFormat)
			.process(findService)
			
//			Find Plan Id
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("{{threescale.dest.url}}/admin/api/services/"
					+ "${body.getId}/application_plans.xml")
			.unmarshal(eventDataFormat)
			.process(findPlan)
//			Remember Plan ID
			.setHeader("PlanID", simple("${body.getId}"))
			
//			Create Application
			.log("Creating synced application . . .")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "plan_id=${header.PlanID}&"
					+ "name=${header.name}&"
					+ "description=${header.description}&"
					+ "application_id=${header.application_id}&"
					+ "application_key=${header.application_key}"))
			.setBody(simple("${null}"))
			.toD("{{threescale.dest.url}}/admin/api/accounts/"
					+ "${header.AccountID}/applications.xml")
			.log("Application created and synced")
		;	
	}
}
