package com.vsp.bd.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Log {

	public static final String ERROR_TYPE = "ERROR";
	public static final String SEARCH_TYPE = "SEARCH";
	public static final String LOGIN_TYPE = "LOGIN";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String type;

	@Lob
	private String value;

	public Log() {
		
	}

	public Log(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
