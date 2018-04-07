package com.vsp.bd.service.parser;

import org.springframework.stereotype.Service;

import com.vsp.bd.domain.Recipe;

@Service
public interface ParserService {

	public Recipe parseRecipe(String recipeUrl);

	public Recipe setSourceWebsite(Recipe recipe);

}
