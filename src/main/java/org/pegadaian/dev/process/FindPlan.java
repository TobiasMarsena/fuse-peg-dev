package org.pegadaian.dev.process;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.pegadaian.dev.model.Plan;
import org.pegadaian.dev.model.Plans;
import org.pegadaian.dev.model.Service;
import org.pegadaian.dev.model.Services;

public class FindPlan implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String planName = exchange.getIn().getHeader("PlanName", String.class);
		List<Plan> plans = exchange.getIn().getBody(Plans.class).getPlans();
		for (Plan plan : plans) {
			if (plan.getName().equals(planName)) {
				exchange.getIn().setBody(plan, Plan.class);
				break;
			}
		}
	}

}
