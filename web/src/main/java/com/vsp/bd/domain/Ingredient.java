package com.vsp.bd.domain;

import java.util.Set;

import javax.persistence.*;;

@Entity
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	private String synonymIds;

	@ManyToMany(mappedBy = "ingredients", cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	private Set<Recipe> recipes;

	public String getSynonymIds() {
		return synonymIds;
	}

	public void setSynonymIds(String synonymIds) {
		this.synonymIds = synonymIds;
	}

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
