package org.pegadaian.dev.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.FIELD)
public class Event {
	
	@XmlElement
	private String action;
	@XmlElement
	private String type;
	@XmlElement(name = "object")
	private Object object;
	
	public Event() {
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public String getApplicationId() {
		return object.getApplication().getApp_id();
	}
	public String getOrg_name() {
		return object.getAccounts().getAccount().get(0).getOrg_name();
	}
	public String getUsername() {
		return object.getAccounts().getAccount().get(0).getUsers().get(0).getUsername();
	}
	public String getEmail() {
		return object.getAccounts().getAccount().get(0).getUsers().get(0).getEmail();
	}	
}
