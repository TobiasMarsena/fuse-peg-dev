package org.pegadaian.dev.process;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.pegadaian.dev.model.Service;
import org.pegadaian.dev.model.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindService implements Processor{
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		String systemName = exchange.getIn().getHeader("ServiceName", String.class);
		List<Service> services = exchange.getIn().getBody(Services.class).getServices();
		for (Service service : services) {
			if (service.getSystem_name().equals(systemName)) {
				exchange.getIn().setBody(service, Service.class);
				break;
			}
		}
	}

}
