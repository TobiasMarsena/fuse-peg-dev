package org.pegadaian.dev.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "plans")
@XmlAccessorType(XmlAccessType.FIELD)
public class Plans {
	@XmlElement(name = "plan")
	private List<Plan> plans = new ArrayList<>();

	public List<Plan> getPlans() {
		return plans;
	}
	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}
}
