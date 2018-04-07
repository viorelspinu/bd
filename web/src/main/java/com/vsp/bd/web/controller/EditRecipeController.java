package com.vsp.bd.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.domain.Tag;
import com.vsp.bd.domain.TagRepository;
import com.vsp.bd.service.IngredientService;
import com.vsp.bd.service.RecipeService;
import com.vsp.bd.service.parser.ParserServiceImpl;
import com.vsp.bd.web.controller.form.EditRecipeForm;

@Controller
@SessionAttributes("editRecipeForm")
public class EditRecipeController extends BaseController {

	@Autowired
	private IngredientService ingredientService;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private RecipeService recipeService;

	@Autowired
	private ParserServiceImpl parserServiceImpl;

	@Autowired
	private TagRepository tagRepository;

	@GetMapping(path = "/view-clasify-recipe")
	public String viewClasifyRecipe(Model model, HttpSession session) {

		Pageable pageRequest = new PageRequest(0, 1);
		List<Integer> recipeList = recipeRepository.findRecipeWithNoTags(pageRequest);
		EditRecipeForm editRecipeForm = new EditRecipeForm();
		editRecipeForm.setId(-1);
		editRecipeForm.setIngredients("");
		editRecipeForm.setTitle("");
		editRecipeForm.setUrl("");
		editRecipeForm.setText("");

		if (recipeList.size() > 0) {
			Recipe recipe = recipeRepository.findById(recipeList.get(0));
			editRecipeForm.setId(recipe.getId());
			editRecipeForm.setTitle(recipe.getTitle());
			editRecipeForm.setIngredients(recipe.getIngredientsAsString());
			editRecipeForm.setUrl(recipe.getRecipeUrl());
		}

		model.addAttribute("editRecipeForm", editRecipeForm);

		Iterable<Tag> tags = tagRepository.findAllOrderByName();
		model.addAttribute("tags", tags);

		Integer clasifiedCount = (Integer) session.getAttribute("clasifiedCount");
		if (clasifiedCount == null) {
			clasifiedCount = 0;
		}
		model.addAttribute("clasifiedCount", clasifiedCount);
		short barWidth = (short) (clasifiedCount * 5);
		barWidth = (short) (barWidth % 100);
			
		model.addAttribute("barWidth", barWidth);

		return "clasify_recipe";
	}

	@PostMapping(path = "/clasify-recipe")
	public String doClasifyRecipe(Model model, @RequestParam(required = true) Integer recipeId,
			@RequestParam(required = true) Integer tagId, HttpSession session) {

		Set<Integer> tagsId = new HashSet<>(1);
		tagsId.add(tagId);
		Set<Integer> recipesId = new HashSet<>(1);
		recipesId.add(recipeId);

		recipeService.addTagsToRecipes(recipesId, tagsId);

		Integer clasifiedCount = (Integer) session.getAttribute("clasifiedCount");
		if (clasifiedCount == null) {
			clasifiedCount = 0;
		}
		clasifiedCount++;
		session.setAttribute("clasifiedCount", clasifiedCount);

		return "redirect:/view-clasify-recipe";
	}

	@PostMapping(path = "/edit/batch-clasify")
	public String batchClasifyRecipe(Model model, @RequestParam(required = true) String recipeIdList,
			@RequestParam(required = true) Integer batchTagId, HttpSession session) throws IOException {

		Set<Integer> recipesId = new HashSet<>();

		try (BufferedReader bufferedReader = new BufferedReader(new StringReader(recipeIdList))) {
			String line = "";
			while (line != null) {
				line = bufferedReader.readLine();
				if (line != null) {
					if (line.length() > 0) {
						line = line.replaceAll(" ", "");
						recipesId.add(Integer.parseInt(line));
					}
				}

			}
		}

		Set<Integer> tagsId = new HashSet<>(1);
		tagsId.add(batchTagId);
		recipeService.addTagsToRecipes(recipesId, tagsId);

		return "redirect:/view-clasify-recipe";
	}

	@PostMapping(path = "/edit/detect-ingredients")
	@ResponseBody
	public String computeIngredients(@RequestParam(required = true) String ingredientsText,
			@RequestParam(required = true) String recipeText) {

		String[] ingredientArray = ingredientsText.split(",");
		Set<String> ingredientNameSet = new HashSet<String>(Arrays.asList(ingredientArray));
		ingredientService.saveIngredientsIfNotThereYet(ingredientNameSet);

		Set<Ingredient> ingredientSet = ingredientService.findIngredientsInRecipeText(recipeText);
		return ingredientService.ingredientsToString(ingredientSet);
	}

	@PostMapping(path = "/edit/save-recipe")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String saveRecipe(Model model, @ModelAttribute EditRecipeForm editRecipeForm) {

		Integer recipeIdInteger = editRecipeForm.getId();

		setMessage(INFO_TYPE, "Recipe " + editRecipeForm.getTitle() + " has been saved");

		if (isDemoAdmin()) {
			return "redirect:/edit/add-recipe?msg=1";
		}

		Recipe recipe = recipeService.buildRecipe(editRecipeForm.getIngredients(), editRecipeForm.getText(),
				editRecipeForm.getTitle(), recipeIdInteger, editRecipeForm.getUrl());

		if (isFullAdmin()) {
			recipeRepository.save(recipe);
			return "redirect:/edit/edit-recipe?recipeId=" + recipe.getId() + "&msg=1";
		}

		return "redirect:/edit/add-recipe?msg=1";

	}

	@GetMapping(path = "/edit/add-recipe")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String addRecipe(Model model, @RequestParam(required = false) Integer msg) {

		if (msg != null) {
			displayMessages(model);
		}
		EditRecipeForm editRecipeForm = new EditRecipeForm();
		editRecipeForm.setId(-1);
		editRecipeForm.setIngredients("");
		editRecipeForm.setTitle("");
		editRecipeForm.setUrl("");
		editRecipeForm.setText("");
		model.addAttribute("editRecipeForm", editRecipeForm);
		return "edit_recipe";
	}

	@GetMapping(path = "/edit/edit-recipe")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String viewEditRecipe(Model model, @RequestParam(required = true) Integer recipeId,
			@RequestParam(required = false) Integer msg) {

		if (msg != null) {
			displayMessages(model);
		}

		if (recipeId == null) {
			return "redirect:/edit/add-recipe";
		}

		EditRecipeForm editRecipeForm = new EditRecipeForm();
		editRecipeForm.setId(recipeId);
		Recipe recipe = recipeRepository.findById(recipeId);
		editRecipeForm.setIngredients(recipe.getIngredientsAsString());
		editRecipeForm.setTitle(recipe.getTitle());
		editRecipeForm.setText(recipe.getText());

		if (recipe.getSourceType() == Recipe.PARSED_FROM_WEBSITE) {
			editRecipeForm.setUrl(recipe.getRecipeUrl());
		} else {
			editRecipeForm.setUrl("");
		}

		editRecipeForm.setStage(recipe.getStage());
		editRecipeForm.setFailureToParseReason(recipe.getFailureToParseReason());

		model.addAttribute("editRecipeForm", editRecipeForm);
		return "edit_recipe";
	}

	@PostMapping(path = "/edit/delete-recipe")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	@ResponseBody
	public String deleteRecipe(Model model, @RequestParam(required = true) Integer recipeId) {
		Recipe recipe = recipeRepository.findById(recipeId);
		if (recipe != null) {
			if (isFullAdmin()) {
				recipeRepository.delete(recipe);
			}
		}
		return "OK";
	}

	@PostMapping(path = "/edit/parse-recipe")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	@ResponseBody
	public Recipe parseRecipe(Model model, @RequestParam(required = true) String recipeUrl) {

		Recipe recipe = parserServiceImpl.parseRecipe(recipeUrl);
		Set<Ingredient> ingredientSet = ingredientService.findIngredientsInRecipeText(recipe.getText());
		recipe.setIngredientsAsString(ingredientService.ingredientsToString(ingredientSet));
		return recipe;
	}

	@GetMapping(path = "/edit/recipe-count-by-url")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	@ResponseBody
	public Integer recipeCountByUrl(@RequestParam(required = true) String url) {

		int count = recipeRepository.findByRecipeUrl(url).size();

		return count;
	}

}
