package com.vsp.bd.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepositoryCustom {

	public static final int ALL_YES_INGREDIENTS = 0;
	public static final int ONE_YES_INGREDIENTS = 1;

	public SearchRecipeResult searchRecipesByIngredients(String ingredientsYes, String ingredientsNo,
			String titleContains, List<Integer> websiteIds, int yesIngredientsAllOrJustOne, int sortType, int page,
			int pageSize);

}
