package org.pegadaian.dev.model;

public class Token {

	private String access_token;
	private int expires_in;
	private int refresh_expires_in;
	private String refresh_token;
	private String scope;
	
	public Token() {
	}
	public Token(String access_token, int expires_in, int refresh_expires_in, String refresh_token, String scope) {
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.refresh_expires_in = refresh_expires_in;
		this.refresh_token = refresh_token;
		this.scope = scope;
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	public int getRefresh_expires_in() {
		return refresh_expires_in;
	}
	public void setRefresh_expires_in(int refresh_expires_in) {
		this.refresh_expires_in = refresh_expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	@Override
	public String toString() {
		return "Token [access_token=" + access_token + ", expires_in=" + expires_in + ", refresh_expires_in="
				+ refresh_expires_in + ", refresh_token=" + refresh_token + ", scope=" + scope + "]";
	}
	
	public String bearerAuth() {
		return "Bearer " + getAccess_token();
	}
	public String refreshBodyRequest() {
		return "grant_type=refresh_token&refresh_token=" + getRefresh_token() + "&client_id=admin-cli";
	}
}
