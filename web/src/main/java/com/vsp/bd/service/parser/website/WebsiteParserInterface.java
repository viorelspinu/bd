package com.vsp.bd.service.parser.website;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.vsp.bd.domain.Recipe;

@Service
public interface WebsiteParserInterface {
	public Recipe parseRecipe(String recipeUrl) throws IOException;

	public boolean isAbleToParse(String url);

}
