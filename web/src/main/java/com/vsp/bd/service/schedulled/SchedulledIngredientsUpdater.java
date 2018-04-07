package com.vsp.bd.service.schedulled;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.service.IngredientService;

@Component
@Transactional
public class SchedulledIngredientsUpdater extends GenericSchedulledProcess {

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private IngredientService ingredientService;

	@Scheduled(fixedDelay = 10)
	public void parseRecipe() {

		if (isLocalhost()) {
			 return;
		}

		Pageable pageRequest = new PageRequest(0, 1);
		List<Integer> recipeIdList = recipeRepository.findRecipeOutdatedIngredients(new Long(10), pageRequest);

		
		
		if (recipeIdList.size() == 0) {
			return;
		} else {
			Recipe recipe = recipeRepository.findById(recipeIdList.get(0));

			recipe.setText(recipe.getText().toLowerCase().replaceAll("\r", ",").replaceAll("\n", ","));

			Set<Ingredient> ingredientSet = ingredientService.findIngredientsInRecipeText(recipe.getText());
			recipe.setIngredients(ingredientSet);

			recipe.setStage(Recipe.STAGE_PARSED);
			recipeRepository.save(recipe);
			System.out.println("parsed ingredient for " + recipe.getTitle());;
		}
	}

}
