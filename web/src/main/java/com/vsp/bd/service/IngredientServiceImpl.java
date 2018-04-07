package com.vsp.bd.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.IngredientRepository;

@Service
@Transactional
public class IngredientServiceImpl implements IngredientService {

	@Autowired
	private IngredientRepository ingredientRepository;

	@Override
	public void saveIngredientsIfNotThereYet(Set<String> ingredientNameSet) {
		Set<String> ingredientNameSetInternal = new HashSet<>();

		for (String name : ingredientNameSet) {
			String nameCorrected = name.trim().toLowerCase();
			if (nameCorrected.length() > 2) {
				ingredientNameSetInternal.add(nameCorrected);
			}
		}

		Set<Ingredient> ingredients = ingredientRepository.findIngredientByNameIsIn(ingredientNameSetInternal);

		for (Ingredient ingredient : ingredients) {
			ingredientNameSetInternal.remove(ingredient.getName().trim().toLowerCase());
		}

		for (String ingredientName : ingredientNameSetInternal) {
			Ingredient ingredient = new Ingredient();
			ingredient.setName(ingredientName);
			ingredientRepository.save(ingredient);
		}
	}

	@Override
	public Set<Ingredient> findIngredientsInRecipeText(String recipeText) {
		Iterable<Ingredient> allIngredientList = ingredientRepository.findAll();

		Set<Ingredient> ingredientSet = new LinkedHashSet<>();

		if (recipeText == null) {
			return ingredientSet;
		}

		recipeText = " " + recipeText.toLowerCase().replaceAll("\r", ",").replaceAll("\n", ",") + " ";
		for (Ingredient ingredient : allIngredientList) {
			String ingredientRex = ingredient.getName().toLowerCase().replaceAll(" ", "\\\\s");
			ingredientRex = ".*\\W+" + ingredientRex + "\\W+.*";
			if (recipeText.matches(ingredientRex)) {
				ingredientSet.add(ingredient);
			}
		}

		return expandIngredientSetWithSynonyms(ingredientSet);
	}

	private Set<Ingredient> expandIngredientSetWithSynonyms(Set<Ingredient> ingredients) {

		Set<Ingredient> newIngredients = new HashSet<Ingredient>();

		for (Ingredient ingredient : ingredients) {
			if (ingredient.getSynonymIds() != null) {
				String[] ids = ingredient.getSynonymIds().split(",");
				for (String idStr : ids) {
					idStr = idStr.trim();
					if (idStr.length() > 0) {
						Integer synonymId = Integer.parseInt(idStr);
						if (synonymId != ingredient.getId()) {
							Ingredient ingredientSynonym = ingredientRepository.findById(synonymId);
							if (ingredientSynonym != null) {
								newIngredients.add(ingredientSynonym);
							}
						}
					}

				}
			}
		}

		ingredients.addAll(newIngredients);
		return ingredients;
	}

	@Override
	public Set<Ingredient> loadIngredientsFromString(String ingredientsString) {
		String[] ingredientArray = ingredientsString.split(",");
		Set<String> ingredientNameSet = new HashSet<String>();

		for (String name : ingredientArray) {
			ingredientNameSet.add(name.trim().toLowerCase());
		}

		saveIngredientsIfNotThereYet(ingredientNameSet);

		Set<Ingredient> ingredientSet = ingredientRepository.findIngredientByNameIsIn(ingredientNameSet);

		return ingredientSet;
	}

	@Override
	public String ingredientsToString(Set<Ingredient> ingredientSet) {

		StringBuilder stringBuilder = new StringBuilder();

		for (Ingredient ingredient : ingredientSet) {
			stringBuilder.append(ingredient.getName());
			stringBuilder.append(", ");
		}

		String ingredientsString = stringBuilder.toString();
		if (ingredientsString.length() > 1) {
			ingredientsString = ingredientsString.substring(0, ingredientsString.length() - 2);
		}

		return ingredientsString;
	}

}
