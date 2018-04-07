package com.vsp.bd.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import com.vsp.bd.service.IngredientService;
import com.vsp.bd.service.StringHelper;

public class RecipeRepositoryImpl implements RecipeRepositoryCustom {

	public static final Integer SORT_ON_TITLE = 1;

	public static final Integer SORT_ON_INGREDIENT_COUNT_ASC = 2;

	public static final Integer SORT_ON_INGREDIENT_COUNT_DESC = 3;

	public static final Integer SORT_ON_BLOG = 4;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private IngredientService ingredientService;

	private Set<Ingredient> prepareSearchIngredientListUsingIsIn(String ingredientString) {

		String[] ingredientArray = ingredientString.split(",");

		boolean ingredientsPresent = true;

		if (ingredientArray.length == 0) {
			ingredientsPresent = false;
		}
		if (ingredientString.length() <= 1) {
			ingredientsPresent = false;
		}

		Set<String> nameSet = new HashSet<String>();

		for (String ingredientNo : ingredientArray) {
			String ingredientName = ingredientNo.trim().toLowerCase();
			if (ingredientName.length() > 0) {
				nameSet.add(ingredientName);
			}
		}

		Set<Ingredient> ingredientsSet = ingredientRepository.findIngredientByNameIsIn(nameSet);

		if (ingredientsSet.size() == 0) {
			ingredientsPresent = false;
		}

		if (ingredientsPresent) {
			return ingredientsSet;
		} else {
			return null;
		}

	}

	private void saveIngredientsIfNotThereYet(String ingredientNames) {
		String[] ingredientNameArray = ingredientNames.split(",");
		Set<String> ingredientNameSet = new HashSet<String>(Arrays.asList(ingredientNameArray));
		ingredientService.saveIngredientsIfNotThereYet(ingredientNameSet);
	}

	@Override
	public SearchRecipeResult searchRecipesByIngredients(String ingredientsYes, String ingredientsNo,
			String nameContains, List<Integer> websiteIds, int yesIngredientsAllOrJustOne, int sortType, int page,
			int pageSize) {

		ingredientsYes = StringHelper.removeDiacritics(ingredientsYes);
		ingredientsNo = StringHelper.removeDiacritics(ingredientsNo);
		nameContains = StringHelper.removeDiacritics(nameContains);

		saveIngredientsIfNotThereYet(ingredientsYes);
		saveIngredientsIfNotThereYet(ingredientsNo);

		Set<Ingredient> ingredientNoList = prepareSearchIngredientListUsingIsIn(ingredientsNo);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT id, title, ingredients_as_string, recipe_url, website_nick from recipe ");

		if (ingredientNoList != null) {
			stringBuilder.append(" WHERE NOT( ");

			boolean first = true;

			for (Ingredient ingredientNo : ingredientNoList) {
				if (!first) {
					stringBuilder.append(" OR ");
				}
				first = false;
				stringBuilder.append("EXISTS");
				stringBuilder.append(
						"(SELECT id FROM recipe_ingredient WHERE recipe_ingredient.recipe_id = recipe.id AND recipe_ingredient.ingredient_id  = "
								+ ingredientNo.getId() + ")");
			}

			stringBuilder.append(") ");
		}

		Set<Ingredient> ingredientYesList = prepareSearchIngredientListUsingIsIn(ingredientsYes);

		String betweenSelectClause;
		if (yesIngredientsAllOrJustOne == RecipeRepositoryCustom.ALL_YES_INGREDIENTS) {
			betweenSelectClause = " AND ";
		} else {
			betweenSelectClause = " OR ";
		}

		if (ingredientYesList != null) {

			if (!stringBuilder.toString().contains(" WHERE ")) {
				stringBuilder.append(" WHERE ( ");
			} else {
				stringBuilder.append(" AND ( ");
			}

			boolean first = true;

			for (Ingredient ingredientYes : ingredientYesList) {
				if (!first) {
					stringBuilder.append(betweenSelectClause);
				}
				first = false;
				stringBuilder.append("EXISTS");
				stringBuilder.append(
						"(SELECT id FROM recipe_ingredient WHERE recipe_ingredient.recipe_id = recipe.id AND recipe_ingredient.ingredient_id  = "
								+ ingredientYes.getId() + ")");
			}

			stringBuilder.append(") ");
		}

		if ((nameContains != null) && (nameContains.trim().length() > 1)) {
			if ((ingredientNoList == null) && (ingredientYesList == null)) {
				stringBuilder.append(" WHERE (");

			} else {
				stringBuilder.append(" AND (");
			}
			stringBuilder.append(" title LIKE '%" + nameContains + "%'");
			stringBuilder.append(" )");
		}

		if (!websiteIds.isEmpty()) {
			if (stringBuilder.toString().contains(" WHERE ")) {
				stringBuilder.append(" AND (");
			} else {
				stringBuilder.append(" WHERE (");
			}
			stringBuilder.append(" website_id IN ( ");
			for (int k = 0; k < websiteIds.size(); k++) {
				stringBuilder.append(websiteIds.get(k));
				if (k != (websiteIds.size() - 1)) {
					stringBuilder.append(", ");
				}
			}
			stringBuilder.append(" ) ");
			stringBuilder.append(" )");
		}

		if (SORT_ON_TITLE.equals(sortType)) {
			stringBuilder.append(" ORDER BY title ASC ");
		}
		if (SORT_ON_INGREDIENT_COUNT_ASC.equals(sortType)) {
			stringBuilder.append(" ORDER BY LENGTH(ingredients_as_string) ASC ");
		}
		if (SORT_ON_INGREDIENT_COUNT_DESC.equals(sortType)) {
			stringBuilder.append(" ORDER BY LENGTH(ingredients_as_string) DESC ");
		}
		if (SORT_ON_BLOG.equals(sortType)) {
			stringBuilder.append(" ORDER BY website_id ASC ");
		}
		// TODO:not the best way; will extract lot of data all the time; might want to
		// refactor sometimes using > id
		stringBuilder.append("LIMIT ");
		stringBuilder.append((page - 1) * pageSize);
		stringBuilder.append(", ");
		stringBuilder.append(pageSize);
		stringBuilder.append(";");

		System.out.println(stringBuilder.toString());

		Query nativeQuery = entityManager.createNativeQuery(stringBuilder.toString());
		List<Object[]> rowList = nativeQuery.getResultList();

		List<Recipe> recipeList = new ArrayList<>();

		for (Object[] row : rowList) {
			Recipe recipe = new Recipe();
			recipe.setId(Integer.parseInt(row[0].toString()));
			if (row[1] != null) {
				recipe.setTitle(row[1].toString());
			}
			if (row[2] != null) {
				recipe.setIngredientsAsString(row[2].toString());
			}
			if (row[3] != null) {
				recipe.setRecipeUrl(row[3].toString());
			}
			if (row[4] != null) {
				recipe.setWebsiteNick(row[4].toString());
			} else {
				recipe.setWebsiteNick("");
			}
			recipeList.add(recipe);
		}

		SearchRecipeResult searchRecipeResult;
		if (recipeList.size() == 0) {
			searchRecipeResult = findOutWhyNoResults(ingredientYesList, nameContains);
		} else {
			searchRecipeResult = new SearchRecipeResult();
		}

		searchRecipeResult.setRecipeList(recipeList);

		return searchRecipeResult;
	}

	private SearchRecipeResult findOutWhyNoResults(Set<Ingredient> ingredientsYes, String nameContains) {
		SearchRecipeResult searchRecipeResult = new SearchRecipeResult();
		Set<Ingredient> yesIngredientsWithNoRecipes = new HashSet<>();

		if (ingredientsYes != null) {
			for (Ingredient ingredient : ingredientsYes) {
				boolean noRecipes = false;
				if (ingredient.getRecipes() == null) {
					noRecipes = true;
				} else {
					if (ingredient.getRecipes().size() == 0) {
						noRecipes = true;
					}
				}
				if (noRecipes) {
					yesIngredientsWithNoRecipes.add(ingredient);
				}
			}
		}

		searchRecipeResult.setNonExistentYesIngredients(yesIngredientsWithNoRecipes);
		if (yesIngredientsWithNoRecipes.size() != 0) {
			searchRecipeResult.setReason(SearchRecipeResult.YES_INGREDIENTS_WITH_NO_RECIPES_EMPTY_REASON);
		} else {
			searchRecipeResult.setReason(SearchRecipeResult.NO_RECIPES_WITH_SELECTED_COMBINATION_EMPTY_REASON);
		}

		Query nativeQuery = entityManager
				.createNativeQuery("SELECT COUNT(id) FROM recipe WHERE title LIKE " + "'%" + nameContains + "%'");
		BigInteger countByNameOnly = (BigInteger) nativeQuery.getSingleResult();
		Long countByNameOnlyLong = countByNameOnly.longValue();
		searchRecipeResult.setRecipeCountUsingOnlyTheName(countByNameOnlyLong);
		if (countByNameOnlyLong == 0) {
			searchRecipeResult.setReason(SearchRecipeResult.NO_RECIPES_WITH_SUCH_NAME_EMPTY_REASON);
		}

		return searchRecipeResult;
	}

}
