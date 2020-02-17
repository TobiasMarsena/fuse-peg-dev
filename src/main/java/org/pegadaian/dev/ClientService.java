package org.pegadaian.dev;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
	
	public ClientService() {}

	public Client changeFlow(Exchange exchange) {
		Client client = exchange.getIn().getBody(Client[].class)[0];
		client.setDirectAccessGrantsEnabled(true);
		client.setStandardFlowEnabled(false);
		return client;
	}
}
