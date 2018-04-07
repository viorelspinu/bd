package com.vsp.bd.web.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.Log;
import com.vsp.bd.domain.LogRepository;
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.domain.RecipeRepositoryCustom;
import com.vsp.bd.domain.RecipeRepositoryImpl;
import com.vsp.bd.domain.SearchRecipeResult;
import com.vsp.bd.domain.SearchTemplate;
import com.vsp.bd.domain.SearchTemplateRepository;
import com.vsp.bd.domain.Website;
import com.vsp.bd.domain.WebsiteRepository;
import com.vsp.bd.service.AccountService;
import com.vsp.bd.service.RecipeService;
import com.vsp.bd.web.controller.form.SearchRecipeForm;

@SessionAttributes("searchRecipeForm")
@Controller
public class SearchRecipeController extends BaseController {

	private static final int PAGE_SIZE = 60;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private RecipeService recipeService;

	@Autowired
	private SearchTemplateRepository searchTemplateRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private WebsiteRepository websiteRepository;

	@RequestMapping(path = "/view-recipe-list")
	public String viewRecipeList(Model model, @RequestParam(required = false) Byte stage) {

		clearMessages();
		Iterable<Recipe> recipes = null;
		if (stage == null) {
			recipes = recipeRepository.findAll();
		} else {
			Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
			recipes = recipeRepository.findRecipeByStageOrderById(stage, pageable);
		}

		for (Recipe recipe : recipes) {
			if (recipe.getText() != null) {
				if (recipe.getText().length() > 30) {
					recipe.setText(recipe.getText().substring(0, 30) + " [...]");
				}
			}
		}
		model.addAttribute("recipeList", recipes);
		model.addAttribute("stage", stage);
		return "recipe_list";
	}

	@RequestMapping(path = "/")
	public String viewSearchRecipe(Model model) {

		SearchRecipeForm searchRecipeForm = new SearchRecipeForm();
		searchRecipeForm.setRecipeList(new ArrayList<>());
		searchRecipeForm.setIngredientsNo("");
		searchRecipeForm.setIngredientsYes("");
		searchRecipeForm.setTitleContains("");
		searchRecipeForm.setPage(1);
		searchRecipeForm.setPageAdvance(0);
		searchRecipeForm.setShouldDisplayNextButton(0);
		searchRecipeForm.setYesIngredientsAllOrJustOne(SearchRecipeForm.ONE);
		searchRecipeForm.setSortType(SearchRecipeForm.SORT_ON_TITLE);
		List<Website> websites = websiteRepository.findAllOrderByDomain();
		searchRecipeForm.setWebsites(websites);
		List<Integer> selectedWebsites = new ArrayList<>();
		for (Website website : websites) {
			selectedWebsites.add(website.getId());
		}
		searchRecipeForm.setSelectedWebsites(selectedWebsites);
		model.addAttribute("searchRecipeForm", searchRecipeForm);

		return "search_recipe";
	}

	@RequestMapping(path = "/public/search")
	public String viewSearchRecipeAfterPost(Model model,
			@ModelAttribute("searchRecipeForm") SearchRecipeForm searchRecipeForm) {
		clearMessages();

		model.addAttribute("ingredientsNo", searchRecipeForm.getIngredientsNo());
		model.addAttribute("ingredientsYes", searchRecipeForm.getIngredientsYes());
		model.addAttribute("titleContains", searchRecipeForm.getTitleContains());
		model.addAttribute("page", searchRecipeForm.getPage());
		model.addAttribute("pageAdvance", searchRecipeForm.getPageAdvance());
		model.addAttribute("recipeList", searchRecipeForm.getRecipeList());
		model.addAttribute("shouldDisplayNextButton", searchRecipeForm.getShouldDisplayNextButton());

		return "search_recipe";
	}

	private void logSearch(SearchRecipeForm searchRecipeForm, List<Recipe> recipeList) {
		Log log = new Log();
		log.setType(Log.SEARCH_TYPE);
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.UK)
				.withZone(ZoneId.of("Europe/Bucharest"));

		StringBuilder stringBuiler = new StringBuilder();
		stringBuiler.append(formatter.format(Instant.now()));
		stringBuiler.append(": NO: ");
		stringBuiler.append(searchRecipeForm.getIngredientsNo());
		stringBuiler.append(" YES");
		if (SearchRecipeForm.ALL.equals(searchRecipeForm.getYesIngredientsAllOrJustOne())) {
			stringBuiler.append(" (ALL) ");
		} else {
			stringBuiler.append(" (ANY) ");
		}
		stringBuiler.append(" : ");
		stringBuiler.append(searchRecipeForm.getIngredientsYes());
		stringBuiler.append(" TITLE: ");
		stringBuiler.append(searchRecipeForm.getTitleContains());

		stringBuiler.append(" WEBSITES: ");
		stringBuiler.append(searchRecipeForm.getSelectedWebsites());

		stringBuiler.append(" -----> ");
		stringBuiler.append(recipeList.size());
		stringBuiler.append(" results");
		if (recipeList.size() > PAGE_SIZE) {
			stringBuiler.append(" and more");
		}
		stringBuiler.append(".");

		log.setValue(stringBuiler.toString());

		logRepository.save(log);
	}

	@PostMapping(path = "/public/do-search-recipe")
	public String doSearchRecipe(Model model, @ModelAttribute SearchRecipeForm searchRecipeForm,
			RedirectAttributes redirectAttrs) {

		clearMessages();

		String ingredientsNoIncludedSearchTemplates = recipeService.explodeIngredientsByIncludingSearchTemplates(
				searchRecipeForm.getIngredientsNo(), getLoggedInAccount());

		Integer pageNew = searchRecipeForm.getPage();

		if (searchRecipeForm.getPageAdvance() == 1) {
			pageNew = searchRecipeForm.getPage() + 1;
		}

		if (searchRecipeForm.getPageAdvance() == -1) {
			pageNew = searchRecipeForm.getPage() - 1;
		}

		if (searchRecipeForm.getPageAdvance() == 0) {
			searchRecipeForm.setPage(1);
			pageNew = 1;
		}

		Integer allOrOne;
		if (SearchRecipeForm.ALL.equals(searchRecipeForm.getYesIngredientsAllOrJustOne())) {
			allOrOne = RecipeRepositoryCustom.ALL_YES_INGREDIENTS;
		} else {
			allOrOne = RecipeRepositoryCustom.ONE_YES_INGREDIENTS;
		}

		Integer sortType = RecipeRepositoryImpl.SORT_ON_TITLE;

		if (SearchRecipeForm.SORT_ON_TITLE.equals(searchRecipeForm.getSortType())) {
			sortType = RecipeRepositoryImpl.SORT_ON_TITLE;
		}
		if (SearchRecipeForm.SORT_ON_INGREDIENT_COUNT_DESC.equals(searchRecipeForm.getSortType())) {
			sortType = RecipeRepositoryImpl.SORT_ON_INGREDIENT_COUNT_DESC;
		}
		if (SearchRecipeForm.SORT_ON_INGREDIENT_COUNT_ASC.equals(searchRecipeForm.getSortType())) {
			sortType = RecipeRepositoryImpl.SORT_ON_INGREDIENT_COUNT_ASC;
		}
		if (SearchRecipeForm.SORT_ON_BLOG.equals(searchRecipeForm.getSortType())) {
			sortType = RecipeRepositoryImpl.SORT_ON_BLOG;
		}

		SearchRecipeResult searchRecipeResult = recipeRepository.searchRecipesByIngredients(
				searchRecipeForm.getIngredientsYes(), ingredientsNoIncludedSearchTemplates,
				searchRecipeForm.getTitleContains(), searchRecipeForm.getSelectedWebsites(), allOrOne, sortType,
				pageNew, PAGE_SIZE + 1);

		List<Recipe> recipeList = searchRecipeResult.getRecipeList();

		searchRecipeForm.setShouldDisplayNextButton(0);
		if (recipeList.size() > PAGE_SIZE) {
			searchRecipeForm.setShouldDisplayNextButton(1);
		}

		logSearch(searchRecipeForm, recipeList);

		searchRecipeForm.setRecipeList(recipeList);
		searchRecipeForm.setIngredientsNo(ingredientsNoIncludedSearchTemplates);
		searchRecipeForm.setPage(pageNew);
		searchRecipeForm.setPageAdvance(0);

		if (recipeList.size() == 0) {
			searchRecipeForm.setSearchExplain(generateSearchExplain(searchRecipeForm, searchRecipeResult));
		} else {
			searchRecipeForm.setSearchExplain(null);
		}
		redirectAttrs.addFlashAttribute("searchRecipeForm", searchRecipeForm);

		return "redirect:/public/search";
	}

	private List<String> generateSearchExplain(SearchRecipeForm searchRecipeForm,
			SearchRecipeResult searchRecipeResult) {
		List<String> explainList = new ArrayList<>();

		StringBuilder stringBuilder;

		if (SearchRecipeResult.NO_RECIPES_WITH_SUCH_NAME_EMPTY_REASON == searchRecipeResult.getReason()) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("* din pacate nu exista nici o reteta care sa contina in nume '");
			stringBuilder.append(searchRecipeForm.getTitleContains());
			stringBuilder.append("'; incearca sa elimini complet acest criteriu de cautare.");
			explainList.add(stringBuilder.toString());
		}

		if (SearchRecipeResult.YES_INGREDIENTS_WITH_NO_RECIPES_EMPTY_REASON == searchRecipeResult.getReason()) {
			for (Ingredient ingredient : searchRecipeResult.getNonExistentYesIngredients()) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("* nu avem nici o reteta care sa foloseasca '");
				stringBuilder.append(ingredient.getName());
				stringBuilder.append("' drept ingredient; incearca sa-l elimini din cautare.");
				explainList.add(stringBuilder.toString());
			}
		}
		if (SearchRecipeResult.NO_RECIPES_WITH_SELECTED_COMBINATION_EMPTY_REASON == searchRecipeResult.getReason()) {
			stringBuilder = new StringBuilder();
			stringBuilder.append(
					"* din pacate nu avem nici o reteta care sa foloseasca combinatia de ingrediente cautata; incearca sa mai elimini din ingrediente.");
			explainList.add(stringBuilder.toString());
		}

		return explainList;
	}

	@ModelAttribute
	protected void populateSearchTemplates(Model model) {
		Account account = getLoggedInAccount();
		Set<SearchTemplate> searchTemplates = null;
		if (account != null) {
			searchTemplates = searchTemplateRepository.findByAccountId(getLoggedInAccount().getId());
		} else {
			searchTemplates = searchTemplateRepository
					.findByAccountId(accountRepository.findByEmail(AccountService.TEMPLATE_ACCOUNT_USERNAME).getId());
		}
		model.addAttribute("searchTemplates", searchTemplates);
	}

}
