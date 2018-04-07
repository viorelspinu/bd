package com.vsp.bd.service.parser.website;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.vsp.bd.domain.Recipe;
import com.vsp.bd.service.StringHelper;

public class WP implements WebsiteParserInterface {

	private List<String> mustHaveQueriesToBeRecipe;
	private String domain;
	private String titleQuery;
	private String bodyQuery;
	private String userAgent;
	private String startBodyHint;
	private List<String> endBodyHints;

	public WP title(String titleQuery) {
		this.titleQuery = titleQuery;
		return this;
	}

	public WP body(String bodyQuery) {
		this.bodyQuery = bodyQuery;
		return this;
	}

	public WP userAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public WP startHint(String startBodyHint) {
		this.startBodyHint = startBodyHint;
		return this;
	}

	public WP endHint(String endBodyHint) {
		this.endBodyHints.add(endBodyHint);
		return this;
	}

	public WP mustHaveQueries(List<String> queries) {
		this.mustHaveQueriesToBeRecipe = queries;
		return this;
	}

	public WP(String domain) {
		this.domain = domain;
		this.endBodyHints = new ArrayList<>();
	}

	public boolean isAbleToParse(String url) {
		return url.toLowerCase().contains(domain);
	}

	@Override
	public Recipe parseRecipe(String recipeUrl) throws IOException {
		Recipe recipe = new Recipe();
		Document doc;
		if (userAgent == null) {
			doc = Jsoup.connect(recipeUrl).timeout(60 * 1000).get();
		} else {
			doc = Jsoup.connect(recipeUrl).userAgent(userAgent).timeout(60 * 1000).get();
		}

		boolean isRecipe = false;

		String text = doc.text().toLowerCase();
		text = StringHelper.removeDiacritics(text);

		Iterator<String> it = mustHaveQueriesToBeRecipe.iterator();

		while ((it.hasNext()) && (!isRecipe)) {
			String query = it.next();
			if (text.contains(query)) {
				isRecipe = true;
			}
		}

		if (!isRecipe) {
			recipe.setStage(Recipe.STAGE_CANT_BE_PARSED);
			recipe.setFailureToParseReason("This article is NOT a recipe, all mustHaveQueriesToBeRecipe had failed");
		} else {
			String ingredients = parseBody(doc);
			String title = parseTitle(doc);

			recipe.setText(ingredients);
			recipe.setTitle(title);

			boolean allDataValid = false;
			if (title != null) {
				if (ingredients != null) {
					if (title.length() > 2) {
						if (ingredients.length() > 0) {
							allDataValid = true;
						}
					}
				}
			}

			if (allDataValid) {
				recipe.setStage(Recipe.STAGE_PARSED);
			} else {
				recipe.setStage(Recipe.STAGE_CANT_BE_PARSED);
				recipe.setFailureToParseReason("Too little data was parsed.");
			}
		}

		return recipe;
	}

	private String parseTitle(Document doc) {
		return doc.select(titleQuery).get(0).text();
	}

	private String parseBody(Document doc) {

		String text = doc.select(bodyQuery).text().toLowerCase();
		text = StringHelper.removeDiacritics(text);
		int start = 0;
		if (startBodyHint != null) {
			start = text.indexOf(startBodyHint.toLowerCase());
		}
		int end = -1;

		for (String endHint : endBodyHints) {
			int currentEnd = text.indexOf(endHint.toLowerCase());
			if (currentEnd > end) {
				end = currentEnd;
			}
		}

		if (endBodyHints.size() == 0) {
			end = text.length() - 1;
		}

		if ((start == -1) || (end == -1)) {
			return "";
		} else {
			text = text.substring(start, end);
		}

		text = StringHelper.removeDiacritics(text);
		text = text.replaceAll("[^\\p{ASCII}]", "");

		return text;
	}

}
