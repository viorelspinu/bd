package com.vsp.bd.domain;

import java.time.Instant;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Account {

	public static final String ROLE_ADMIN_DEMO = "ROLE_ADMIN_DEMO";

	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String email;

	@Column
	private String password;

	@Column
	private String role;

	@Column
	private Instant created;

	@Column
	@OneToMany(mappedBy = "account")
	private Set<SearchTemplate> searchTemplates;

	public Set<SearchTemplate> getSearchTemplates() {
		return searchTemplates;
	}

	public void setSearchTemplates(Set<SearchTemplate> searchTemplates) {
		this.searchTemplates = searchTemplates;
	}

	public Account() {

	}

	public Account(String email, String password, String role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

}
