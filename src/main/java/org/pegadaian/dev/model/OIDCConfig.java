package org.pegadaian.dev.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "oidc_configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class OIDCConfig {
	@XmlElement(name = "standard_flow_enabled")
	private String standardFlowEnabled;
	@XmlElement(name = "direct_access_grants_enabled")
	private String directAccessGrantsEnabled;
	public OIDCConfig() {
	}
	public String isStandardFlowEnabled() {
		return standardFlowEnabled;
	}
	public void setStandardFlowEnabled(String standardFlowEnabled) {
		this.standardFlowEnabled = standardFlowEnabled;
	}
	public String isDirectAccessGrantsEnabled() {
		return directAccessGrantsEnabled;
	}
	public void setDirectAccessGrantsEnabled(String directAccessGrantsEnabled) {
		this.directAccessGrantsEnabled = directAccessGrantsEnabled;
	}		
}
