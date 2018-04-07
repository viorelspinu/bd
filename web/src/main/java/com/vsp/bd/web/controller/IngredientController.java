package com.vsp.bd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.IngredientRepository;
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;

@Controller
public class IngredientController extends BaseController {

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@RequestMapping(path = "/edit/view-ingredient-list")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String viewIngredientsList(Model model, @RequestParam(required = false) Integer msg) {

		if (msg != null) {
			displayMessages(model);
		}

		Iterable<Ingredient> ingredients = ingredientRepository.findAllOrderByName();
		model.addAttribute("ingredientList", ingredients);

		return "ingredient_list";
	}

	@GetMapping(path = "/edit/delete-ingredient")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String deleteIngredient(Model model, @RequestParam(required = true) Integer ingredientId) {

		Ingredient ingredient = ingredientRepository.findById(ingredientId);
		if (isFullAdmin()) {
			for (Recipe recipe : ingredient.getRecipes()) {
				recipe.getIngredients().remove(ingredient);
				// @PreUpdate does not fire when updating collections only and not fields
				recipe.beforeUpdate();
				recipeRepository.save(recipe);
			}
			ingredient = ingredientRepository.findById(ingredientId);
			ingredientRepository.delete(ingredient);
		}
		setMessage(INFO_TYPE, "Ingredient " + ingredient.getName() + " has been deleted");

		return "redirect:/edit/view-ingredient-list?msg=1";

	}

	@PostMapping(path = "/edit/save-ingredient-synonyms")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String saveIngredient(Model model, @RequestParam(required = true) Integer ingredientId,
			@RequestParam(required = true) String synonymIds) {

		if (isFullAdmin()) {
			Ingredient ingredient = ingredientRepository.findById(ingredientId);
			ingredient.setSynonymIds(synonymIds);
			ingredientRepository.save(ingredient);
		}

		return "OK";
	}

}
