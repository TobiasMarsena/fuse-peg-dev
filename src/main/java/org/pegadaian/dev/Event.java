package org.pegadaian.dev;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

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
	
	@XmlRootElement(name = "object")
	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Object {
		@XmlElement(name = "application")
		private Application application;
		
		public Object() {
		}
		public Application getApplication() {
			return application;
		}
		public void setApplication(Application application) {
			this.application = application;
		}

		@XmlRootElement(name = "application")
		@XmlAccessorType(XmlAccessType.FIELD)
		private static class Application {
			@XmlElement
			private String id;
			@XmlElement(name = "application_id")
			private String app_id;
			@XmlElement(name = "oidc_configuration")
			private OIDCConfig oidc;
			
			public Application() {
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getApp_id() {
				return app_id;
			}
			public void setApp_id(String app_id) {
				this.app_id = app_id;
			}
			public OIDCConfig getOidc() {
				return oidc;
			}
			public void setOidc(OIDCConfig oidc) {
				this.oidc = oidc;
			}

			@XmlRootElement(name = "oidc_configuration")
			@XmlAccessorType(XmlAccessType.FIELD)
			private static class OIDCConfig {
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
		}
	}
	
	
}
