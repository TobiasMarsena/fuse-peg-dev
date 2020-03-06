package org.pegadaian.dev.model;

import org.apache.camel.Exchange;

public class Client {

	private String id;
	private String clientId;
	private boolean standardFlowEnabled;
	private boolean directAccessGrantsEnabled;
	
	public Client() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public boolean isStandardFlowEnabled() {
		return standardFlowEnabled;
	}
	public void setStandardFlowEnabled(boolean standardFlowEnabled) {
		this.standardFlowEnabled = standardFlowEnabled;
	}
	public boolean isDirectAccessGrantsEnabled() {
		return directAccessGrantsEnabled;
	}
	public void setDirectAccessGrantsEnabled(boolean directAccessGrantsEnabled) {
		this.directAccessGrantsEnabled = directAccessGrantsEnabled;
	}
	@Override
	public String toString() {
		return "Client [id=" + id + ", clientId=" + clientId + ", standardFlowEnabled=" + standardFlowEnabled
				+ ", directAccessGrantsEnabled=" + directAccessGrantsEnabled + "]";
	}
}
