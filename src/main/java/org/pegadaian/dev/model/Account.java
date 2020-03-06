package org.pegadaian.dev.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "account")
@XmlAccessorType(XmlAccessType.FIELD)
public class Account {
	@XmlElement
	private int id;
	@XmlElement
	private String org_name;
	@XmlElement
	private Users users;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public List<User> getUsers() {
		return users.getUsers();
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public int getUserId() {
		return getUsers().get(0).getId();
	}
}
