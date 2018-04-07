package com.vsp.bd.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.domain.SearchTemplate;
import com.vsp.bd.domain.SearchTemplateRepository;
import com.vsp.bd.domain.Tag;
import com.vsp.bd.domain.TagRepository;
import com.vsp.bd.service.parser.ParserService;

@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private IngredientService ingredientService;

	@Autowired
	private SearchTemplateRepository searchTemplateRepository;

	@Autowired
	private ParserService parserService;

	@Autowired
	private TagRepository tagRepository;

	@Override
	public Recipe buildRecipe(String ingredients, String recipeText, String recipeTitle, Integer recipeId,
			String recipeUrl) {

		Recipe recipe = null;
		if (recipeId != null) {
			recipe = recipeRepository.findById(recipeId);
		}
		if (recipe == null) {
			recipe = new Recipe();
		}

		if (recipeText == null) {
			recipeText = "";
		}
		if (recipeTitle == null) {
			recipeTitle = "";
		}

		recipe.setText(recipeText.toLowerCase().replaceAll("\r", ",").replaceAll("\n", ","));
		recipe.setTitle(recipeTitle);

		recipe.setSourceType(Recipe.MANUALLY_CREATED);
		if (recipeUrl != null) {
			if (recipeUrl.trim().length() > 0) {
				recipe.setSourceType(Recipe.PARSED_FROM_WEBSITE);
				recipe.setRecipeUrl(recipeUrl);
			}
		}

		Set<Ingredient> ingredientSet = ingredientService.loadIngredientsFromString(ingredients);
		recipe.setIngredients(ingredientSet);
		for (Ingredient ingredient : ingredientSet) {
			if (ingredient.getRecipes() == null) {
				ingredient.setRecipes(new HashSet<>());
			}
		}

		recipe.setStage(Recipe.STAGE_PARSED);

		recipe = parserService.setSourceWebsite(recipe);

		return recipe;

	}

	@Override
	public String explodeIngredientsByIncludingSearchTemplates(String ingredients, Account account) {
		if (account == null) {
			return ingredients;
		}
		StringBuilder stringBuilder = new StringBuilder();
		String[] ingredientsArray = ingredients.split(",");
		for (String ingredient : ingredientsArray) {
			ingredient = ingredient.trim();
			if (ingredient != null) {
				if (ingredient.length() > 0) {
					Set<SearchTemplate> searchTemplates = searchTemplateRepository
							.findByNameIgnoreCaseAndAccount_Id(ingredient.toLowerCase(), account.getId());
					if (searchTemplates.size() > 0) {
						for (SearchTemplate searchTemplate : searchTemplates) {
							stringBuilder.append(searchTemplate.getSearchText());
							stringBuilder.append(",");
						}
					} else {
						stringBuilder.append(ingredient);
						stringBuilder.append(",");
					}
				}
			}
		}

		String ret = stringBuilder.toString();
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1).trim();
		}
		return ret;
	}

	@Override
	public boolean saveRecipeToBeParsedLater(String url) {

		Recipe recipeAlreadyInDatabase = recipeRepository.findOneByRecipeUrl(url);
		if (recipeAlreadyInDatabase != null) {
			return false;
		}

		Recipe recipe = new Recipe();
		recipe.setRecipeUrl(url);
		recipe.setStage(Recipe.STAGE_URL_SAVED);
		recipe.setSourceType(Recipe.PARSED_FROM_WEBSITE);
		recipeRepository.save(recipe);

		return true;
	}

	@Override

	public void addTagsToRecipes(Set<Integer> recipesId, Set<Integer> tagsId) {

		for (Integer recipeId : recipesId) {
			Recipe recipe = recipeRepository.findById(recipeId);
			Set<Tag> tags = recipe.getTags();
			for (Integer tagId : tagsId) {
				Tag tag = tagRepository.findById(tagId);
				tags.add(tag);
			}
			recipeRepository.save(recipe);
		}

	}

}
