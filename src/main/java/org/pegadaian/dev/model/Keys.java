package org.pegadaian.dev.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "keys")
@XmlAccessorType(XmlAccessType.FIELD)
public class Keys {
	@XmlElement(name = "key")
	private List<String> keys = new ArrayList<>();

	public List<String> getKeys() {
		return keys;
	}
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
}
