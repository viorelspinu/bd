package com.vsp.bd.service.schedulled;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.service.IngredientService;
import com.vsp.bd.service.parser.ParserService;

@Component
@Transactional
public class SchedulledRecipeParser extends GenericSchedulledProcess {

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private IngredientService ingredientService;

	@Autowired
	private ParserService parserService;

	@Scheduled(fixedDelay = 1000)
	public void parseRecipe() {

		if (isLocalhost()) {
			return;
		}

		Pageable pageRequest = new PageRequest(0, 1);
		List<Integer> recipeIdList = recipeRepository.findRecipeNotYetParsed(pageRequest);

		if (recipeIdList.size() == 0) {
			return;
		} else {
			Recipe recipe = recipeRepository.findById(recipeIdList.get(0));

			Recipe parsedRecipe = parserService.parseRecipe(recipe.getRecipeUrl());

			recipe.setSourceType(Recipe.PARSED_FROM_WEBSITE);
			recipe.setText(parsedRecipe.getText());
			recipe.setTitle(parsedRecipe.getTitle());
			recipe.setWebsite(parsedRecipe.getWebsite());
			recipe.setWebsiteNick(parsedRecipe.getWebsiteNick());
			recipe.setIngredients(ingredientService.findIngredientsInRecipeText(recipe.getText()));

			recipe.setStage(parsedRecipe.getStage());
			recipe.setFailureToParseReason(parsedRecipe.getFailureToParseReason());

			recipeRepository.save(recipe);

		}

	}

}
