package com.vsp.bd.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({ "website" })
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	public static final byte MANUALLY_CREATED = 0;
	public static final byte PARSED_FROM_WEBSITE = 1;

	@Column
	private Byte sourceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "website_id", nullable = true)
	private Website website;

	@Column
	private String title;

	@Lob
	private String text;

	@Column
	private String recipeUrl;

	@Column
	private String failureToParseReason;

	@Column
	private String websiteNick;
	
	public static final byte STAGE_PARSED = 1;
	public static final byte STAGE_URL_SAVED = 2;
	public static final byte STAGE_CANT_BE_PARSED = 3;

	@Column
	private Byte stage;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "recipe_ingredient", joinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "ingredient_id", referencedColumnName = "id"))
	private Set<Ingredient> ingredients;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "recipe_tag", joinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
	private Set<Tag> tags;
	
	@Lob
	private String ingredientsAsString;

	private Long lastUpdateTimestamp;

	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public String getIngredientsAsString() {
		return ingredientsAsString;
	}

	public void setIngredientsAsString(String ingredientsAsString) {
		this.ingredientsAsString = ingredientsAsString;
	}

	public Set<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(Set<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@PrePersist
	@PreUpdate
	public void beforeUpdate() {
		computeIngredients();
		updateTimestamp();
	}

	private void computeIngredients() {
		if (ingredients == null) {
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (Ingredient ingredient : ingredients) {
			stringBuilder.append(ingredient.getName());
			stringBuilder.append(", ");
		}
		this.ingredientsAsString = stringBuilder.toString();
		if (this.ingredientsAsString.length() > 3) {
			this.ingredientsAsString = this.ingredientsAsString.substring(0, this.ingredientsAsString.length() - 2);
		}

	}

	private void updateTimestamp() {
		Long timestamp = System.currentTimeMillis();
		this.lastUpdateTimestamp = timestamp;

	}

	public byte getSourceType() {
		if (sourceType == null) {
			return Recipe.MANUALLY_CREATED;
		}
		return sourceType;
	}

	public void setSourceType(byte sourceType) {
		this.sourceType = sourceType;
	}

	public String getRecipeUrl() {
		return recipeUrl;
	}

	public void setRecipeUrl(String recipeUrl) {
		this.recipeUrl = recipeUrl;
	}

	public Byte getStage() {
		return stage;
	}

	public Website getWebsite() {
		return website;
	}

	public void setWebsite(Website website) {
		this.website = website;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public void setStage(Byte stage) {
		this.stage = stage;
	}

	public String getFailureToParseReason() {
		return failureToParseReason;
	}

	public void setFailureToParseReason(String failureToParseReason) {
		this.failureToParseReason = failureToParseReason;
	}

	public String getWebsiteNick() {
		return websiteNick;
	}

	public void setWebsiteNick(String websiteNick) {
		this.websiteNick = websiteNick;
	}

}
