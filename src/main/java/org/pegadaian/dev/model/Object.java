package org.pegadaian.dev.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
public class Object {
	@XmlElement(name = "application")
	private App application;
	@XmlElement(name = "accounts")
	private Accounts accounts;
	@XmlElement(name = "services")
	private Services services;
	@XmlElement(name = "plans")
	private Plans plans;
	
	public Object() {
	}
	public App getApplication() {
		return application;
	}
	public void setApplication(App application) {
		this.application = application;
	}
	public Accounts getAccounts() {
		return accounts;
	}
	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}
	public Services getServices() {
		return services;
	}
	public void setService(Services services) {
		this.services = services;
	}
	public Plans getPlans() {
		return plans;
	}
	public void setPlans(Plans plans) {
		this.plans = plans;
	}
	public void setServices(Services services) {
		this.services = services;
	}
}
