package com.vsp.bd.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.Recipe;

@Service
public interface RecipeService {

	String explodeIngredientsByIncludingSearchTemplates(String ingredients, Account account);

	Recipe buildRecipe(String ingredients, String recipeText, String recipeTitle, Integer recipeId, String recipeUrl);

	boolean saveRecipeToBeParsedLater(String url);

	void addTagsToRecipes(Set<Integer> recipeId, Set<Integer> tagsId);
}
