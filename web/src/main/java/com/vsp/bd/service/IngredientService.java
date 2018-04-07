package com.vsp.bd.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.vsp.bd.domain.Ingredient;

@Service
public interface IngredientService {

	public void saveIngredientsIfNotThereYet(Set<String> ingredientName);

	public Set<Ingredient> findIngredientsInRecipeText(String recipeText);

	public Set<Ingredient> loadIngredientsFromString(String ingredientsString);

	public String ingredientsToString(Set<Ingredient> ingredients);

}
