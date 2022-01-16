package org.pegadaian.dev.route;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.pegadaian.dev.model.Accounts;
import org.pegadaian.dev.model.Event;
import org.pegadaian.dev.process.FindAccount;
import org.pegadaian.dev.process.FindPlan;
import org.pegadaian.dev.process.FindService;
import org.pegadaian.dev.process.GetEmails;
import org.springframework.stereotype.Component;

@Component
public class WebHookRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		Processor findService = new FindService();
		Processor findPlan = new FindPlan();
		Processor findAccount = new FindAccount();
		Processor getEmails = new GetEmails();
		JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
		JaxbDataFormat eventDataFormat = new JaxbDataFormat(jaxbContext);
		JAXBContext jaxbContext2 = JAXBContext.newInstance(Accounts.class);
		JaxbDataFormat accountsDataFormat = new JaxbDataFormat(jaxbContext2);
		
		
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
			.path("/ping")
			.get().id("ping-webhook")
				.description("Ping endpoint")
				.responseMessage().code(200).message("Ping Successful").endResponseMessage()
				.to("direct:ping")
			;
		
		onException(Exception.class)
			.maximumRedeliveries(0)
		;
		
		from("direct:ping")
			.setBody(simple("PONG"))
			.log("${body}")
		;
		
		from("timer:test?repeatCount=1")
			.log("Testing SSL to 3Scale SRC")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.source.api}}"))
			.to("https4://{{threescale.source.url}}/admin/api/accounts.xml")
			.log("${headers.CamelHttpResponseCode} ${headers.CamelHttpResponseText}")
			
			.log("Testing SSL to 3Scale DEST")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.to("https4://{{threescale.dest.url}}/admin/api/accounts.xml")
			.log("${headers.CamelHttpResponseCode} ${headers.CamelHttpResponseText}")
			
			.log("Fuse sync ready.")
		;
		
		from("direct:webhookType").id("Content Based Router")
			.unmarshal(eventDataFormat)
			.choice()
				.when().simple("${body.getType} == 'account' && ${body.getAction} == 'created'")
					.to("direct:accountCreated")
				.when().simple("${body.getType} == 'account' && ${body.getAction} == 'deleted'")
					.to("direct:accountDeleted")
				.when().simple("${body.getType} == 'application' && ${body.getAction} == 'created'")
					.to("direct:appCreated")
				.when().simple("${body.getType} == 'application' && ${body.getAction} == 'deleted'")
					.to("direct:appDeleted")
				.when().simple("${body.getType} == 'application' && ${body.getAction} == 'key_updated'")
					.to("direct:keyUpdated")
			.endChoice()
		;
		
		from("direct:accountDeleted")
//			Find Account Id
			.log("Searching for Account to delete . . .")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.source.api}}"))
			.to("https4://{{threescale.source.url}}/admin/api/accounts.xml")
			.unmarshal(accountsDataFormat)
			.process(getEmails)
			
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.setBody(simple(null))
			.to("https4://{{threescale.dest.url}}/admin/api/accounts.xml")
			.unmarshal(accountsDataFormat)
			.process(findAccount)
			
			.log("Account found. Deleting . . .")
//			Delete Account
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
					+ "${body.getId}.xml")
			.log("Account deleted synchronously.")
		;
		
		from("direct:keyUpdated")
			.setHeader("new_application_key", simple("${body.getObject.getApplication.getKeys.getKeys.get(0)}"))
//			Delete current key
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "app_id=${body.getObject.getApplication.getApp_id}"))
			.to("https4://{{threescale.dest.url}}/admin/api/applications/find.xml")
			.unmarshal(eventDataFormat)
			
			.setHeader("AccountID", simple("${body.getAccount_id}"))
			.setHeader("AppID", simple("${body.getId}"))
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
					+ "${body.getAccount_id}/applications/"
					+ "${body.getId}/keys/"
					+ "${body.getKeys.getKeys.get(0)}.xml")
			.log("Old key deleted. Creating new synced key . . .")
			
//			Create new synced key
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "key=${header.new_application_key}"))
			.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
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
			.to("https4://{{threescale.dest.url}}/admin/api/signup.xml")
			.unmarshal(eventDataFormat)

//			Approve Account
			.log("Account has been created with Organization Name ${body.getOrg_name}")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("PUT"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
					+ "${body.getId}/approve.xml")
			.unmarshal(eventDataFormat)

//			Activate User
			.log("Account approved.")
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("PUT"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
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
			.toD("https4://{{threescale.source.url}}/admin/api/accounts/"
					+ "${body.getObject.getApplication.getAccount_id}.xml")
			.unmarshal(eventDataFormat)
			
//			Find Account id
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
					+ "username=${body.getUsers().get(0).getUsername()}&"
					+ "email=${body.getUsers().get(0).getEmail()}"))
			.to("https4://{{threescale.dest.url}}/admin/api/accounts/find.xml")
			.unmarshal(eventDataFormat)
//			Remember Account ID
			.setHeader("AccountID", simple("${body.getId}"))
			
//			Get Service
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.source.api}}"))
			.toD("https4://{{threescale.source.url}}/admin/api/services/"
					+ "${header.ServiceID}.xml")
			.unmarshal(eventDataFormat)
			.setHeader("ServiceName", simple("${body.getSystem_name}"))
			
//			Find Service ID
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.to("https4://{{threescale.dest.url}}/admin/api/services.xml")
			.unmarshal(eventDataFormat)
			.process(findService)
			
//			Find Plan Id
			.removeHeaders("Camel*")
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
			.toD("https4://{{threescale.dest.url}}/admin/api/services/"
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
			.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
					+ "${header.AccountID}/applications.xml")
			.log("Application created and synced")
		;	
		
		from("direct:appDeleted")
//		Find application
		.log("Searching for application to delete . . .")
		.removeHeaders("Camel*")
		.setHeader(Exchange.HTTP_METHOD, constant("GET"))
		.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}&"
				+ "app_id=${body.getObject.getApplication.getApp_id}"))
		.to("https4://{{threescale.dest.url}}/admin/api/applications/find.xml")
		.unmarshal(eventDataFormat)
		.setHeader("AccountID", simple("${body.getAccount_id}"))
		.setHeader("AppID", simple("${body.getId}"))
		
		.log("Application found. Deleting . . .")
//		Delete application
		.removeHeaders("Camel*")
		.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
		.setHeader(Exchange.HTTP_QUERY, simple("access_token={{threescale.dest.api}}"))
		.toD("https4://{{threescale.dest.url}}/admin/api/accounts/"
				+ "${body.getAccount_id}/applications/"
				+ "${body.getId}.xml")
		.log("Application deleted synchronously.")
	;
	}
}
