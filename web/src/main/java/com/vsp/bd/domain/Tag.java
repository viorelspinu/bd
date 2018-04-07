package com.vsp.bd.domain;

import java.util.Set;

import javax.persistence.*;;

@Entity
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	@ManyToMany(mappedBy = "tags", cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	private Set<Recipe> recipes;

	public Set<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(Set<Recipe> recipes) {
		this.recipes = recipes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
