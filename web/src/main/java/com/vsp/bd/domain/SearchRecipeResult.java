package com.vsp.bd.domain;

import java.util.List;
import java.util.Set;

public class SearchRecipeResult {

	public static final byte YES_INGREDIENTS_WITH_NO_RECIPES_EMPTY_REASON = 0;
	public static final byte NO_RECIPES_WITH_SELECTED_COMBINATION_EMPTY_REASON = 1;
	public static final byte NO_RECIPES_WITH_SUCH_NAME_EMPTY_REASON = 2;

	
	private byte reason;

	private List<Recipe> recipeList;

	private Set<Ingredient> nonExistentYesIngredients;

	private Long recipeCountUsingOnlyTheName;

	public byte getReason() {
		return reason;
	}

	public void setReason(byte reason) {
		this.reason = reason;
	}

	public List<Recipe> getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(List<Recipe> recipeList) {
		this.recipeList = recipeList;
	}

	public Set<Ingredient> getNonExistentYesIngredients() {
		return nonExistentYesIngredients;
	}

	public void setNonExistentYesIngredients(Set<Ingredient> nonExistentYesIngredients) {
		this.nonExistentYesIngredients = nonExistentYesIngredients;
	}

	public Long getRecipeCountUsingOnlyTheName() {
		return recipeCountUsingOnlyTheName;
	}

	public void setRecipeCountUsingOnlyTheName(Long recipeCountUsingOnlyTheName) {
		this.recipeCountUsingOnlyTheName = recipeCountUsingOnlyTheName;
	}

}
