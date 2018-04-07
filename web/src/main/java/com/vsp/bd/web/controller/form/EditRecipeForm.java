package com.vsp.bd.web.controller.form;

public class EditRecipeForm {
	private String ingredients;
	private String text;
	private String title;
	private Integer id;
	private String url;

	private Byte stage;
	private String failureToParseReason;

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getText() {
		return text;
	}

	public void setText(String recipeText) {
		this.text = recipeText;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String recipeTitle) {
		this.title = recipeTitle;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer recipeId) {
		this.id = recipeId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String recipeUrl) {
		this.url = recipeUrl;
	}

	public Byte getStage() {
		return stage;
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

}
