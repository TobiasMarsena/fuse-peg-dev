package org.pegadaian.dev.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "services")
@XmlAccessorType(XmlAccessType.FIELD)
public class Services {
	@XmlElement(name = "service")
	private List<Service> services = new ArrayList<>();

	public List<Service> getServices() {
		return services;
	}
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
}
