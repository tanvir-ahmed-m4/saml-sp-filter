package com.metafour.auth.saml.store;

import java.util.Date;
import java.util.Map;

public class SAMLSessionInfo {
	private String nameId;
	private Map<String, String> attributes;
	private Date validTo;

	public SAMLSessionInfo(String nameId, Map<String, String> attributes, Date validTo) {
		this.nameId = nameId;
		this.attributes = attributes;
		this.validTo = validTo;
	}
	// getters should be defined below

	public String getNameId() {
		return nameId;
	}

	public void setNameId(String nameId) {
		this.nameId = nameId;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	
}