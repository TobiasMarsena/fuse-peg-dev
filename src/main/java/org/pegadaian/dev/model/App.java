package org.pegadaian.dev.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
public class App {
	@XmlElement
	private int id;
	@XmlElement(name = "user_account_id")
	private int account_id;
	@XmlElement(name = "service_id")
	private int service_id;
	@XmlElement(name = "application_id")
	private String app_id;
	@XmlElement(name = "keys")
	private Keys keys;
	@XmlElement(name = "plan")
	private Plan plan;
	@XmlElement(name = "oidc_configuration")
	private OIDCConfig oidc;
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "description")
	private String description;
	
	public App() {
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	public int getService_id() {
		return service_id;
	}
	public void setService_id(int service_id) {
		this.service_id = service_id;
	}
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public Keys getKeys() {
		return keys;
	}
	public void setKeys(Keys keys) {
		this.keys = keys;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public OIDCConfig getOidc() {
		return oidc;
	}
	public void setOidc(OIDCConfig oidc) {
		this.oidc = oidc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	
}
