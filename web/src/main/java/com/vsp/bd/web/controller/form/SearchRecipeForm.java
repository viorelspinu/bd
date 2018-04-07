package com.vsp.bd.web.controller.form;

import java.util.List;

import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.Website;

public class SearchRecipeForm {
	private List<Recipe> recipeList;
	private String ingredientsNo;
	private String ingredientsYes;
	private String titleContains;
	private Integer page;
	private Integer pageAdvance;
	private Integer shouldDisplayNextButton;
	private String yesIngredientsAllOrJustOne;
	private List<String> searchExplain;
	private Integer sortType;
	private List<Website> websites;
	private List<Integer> selectedWebsites;

	public static final String ALL = "ALL";
	public static final String ONE = "ONE";

	public static final Integer SORT_ON_TITLE = 1;
	public static final Integer SORT_ON_INGREDIENT_COUNT_ASC = 2;
	public static final Integer SORT_ON_INGREDIENT_COUNT_DESC = 3;
	public static final Integer SORT_ON_BLOG = 4;

	public List<String> getSearchExplain() {
		return searchExplain;
	}

	public void setSearchExplain(List<String> searchExplain) {
		this.searchExplain = searchExplain;
	}

	public String getYesIngredientsAllOrJustOne() {
		return yesIngredientsAllOrJustOne;
	}

	public void setYesIngredientsAllOrJustOne(String yesIngredientsAllOrJustOne) {
		this.yesIngredientsAllOrJustOne = yesIngredientsAllOrJustOne;
	}

	public List<Recipe> getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(List<Recipe> recipeList) {
		this.recipeList = recipeList;
	}

	public String getIngredientsNo() {
		return ingredientsNo;
	}

	public void setIngredientsNo(String ingredientsNo) {
		this.ingredientsNo = ingredientsNo;
	}

	public String getIngredientsYes() {
		return ingredientsYes;
	}

	public void setIngredientsYes(String ingredientsYes) {
		this.ingredientsYes = ingredientsYes;
	}

	public String getTitleContains() {
		return titleContains;
	}

	public void setTitleContains(String titleContains) {
		this.titleContains = titleContains;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageAdvance() {
		return pageAdvance;
	}

	public void setPageAdvance(Integer pageAdvance) {
		this.pageAdvance = pageAdvance;
	}

	public Integer getShouldDisplayNextButton() {
		return shouldDisplayNextButton;
	}

	public void setShouldDisplayNextButton(Integer shouldDisplayNextButton) {
		this.shouldDisplayNextButton = shouldDisplayNextButton;
	}

	public List<Website> getWebsites() {
		return websites;
	}

	public void setWebsites(List<Website> websites) {
		this.websites = websites;
	}

	public List<Integer> getSelectedWebsites() {
		return selectedWebsites;
	}

	public void setSelectedWebsites(List<Integer> selectedWebsites) {
		this.selectedWebsites = selectedWebsites;
	}

	public Integer getSortType() {
		return sortType;
	}

	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}

}
