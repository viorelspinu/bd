package com.vsp.bd.service.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsp.bd.domain.ParserConfigData;
import com.vsp.bd.domain.ParserConfigDataRepository;
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.Website;
import com.vsp.bd.domain.WebsiteRepository;
import com.vsp.bd.service.parser.website.WP;
import com.vsp.bd.service.parser.website.WebsiteParserInterface;

@Service
@Transactional
public class ParserServiceImpl implements ParserService {

	@Autowired
	private ParserConfigDataRepository parserConfigDataRepository;

	@Autowired
	private WebsiteRepository websiteRepository;

	private List<WebsiteParserInterface> wp;

	@PostConstruct
	public void initWebParsers() {

		List<String> qtbr = parserConfigDataRepository
				.findValueByTypeOrderByIdDesc(ParserConfigData.MUST_HAVE_TO_BE_RECIPE_QUERY);

		wp = new ArrayList<>();

		wp.add(new WP("adihadean.ro").mustHaveQueries(qtbr).title("h1").body(".post_content_wrapper")
				.endHint("Facebook Twitter Pinterest Google"));

		wp.add(new WP("teoskitchen.ro").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content")
				.endHint("Rating:"));

		wp.add(new WP("desertdecasa.ro").mustHaveQueries(qtbr).title(".single-post-title").body(".post_content")
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.endHint("si alte retete delicioase").endHint("mai multe retete").endHint("voturi"));

		wp.add(new WP("retetelemeledragi.com").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content")
				.endHint("distribuie acest articol"));

		wp.add(new WP("savoriurbane.com").mustHaveQueries(qtbr).title(".ERSName").body(".td-post-content")
				.endHint("mai multe retete").endHint("si alte retete de").endHint("aici gasiti mai multe")
				.endHint("loading..."));

		wp.add(new WP("biscuim.ro").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content"));

		wp.add(new WP("blogculinar.ro").mustHaveQueries(qtbr).title("h1").body(".postItem")
				.startHint("multam pentru vizita").endHint("vezi alte retete"));

		wp.add(new WP("caietulcuretete.com").mustHaveQueries(qtbr).title(".post-title").body(".entry-content")
				.endHint("distribuie daca iti place").endHint("loading..."));

		wp.add(new WP("e-retete.ro").mustHaveQueries(qtbr).title(".list-title").body(".ingredients"));

		wp.add(new WP("eusipiticulmeu.com").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content")
				.endHint("comentarii"));

		wp.add(new WP("lalena.ro").mustHaveQueries(qtbr).title(".titlu_reteta").body("#ingrediente_reteta"));

		wp.add(new WP("mamamag.ro").mustHaveQueries(qtbr).title("h1").body(".post_wrapper")
				.endHint("intr-un comentariu mai jos").endHint("facebook comments"));

		wp.add(new WP("prajituricisialtele.ro").mustHaveQueries(qtbr).title(".post-title").body(".inner-post-entry")
				.endHint("was last modified"));

		wp.add(new WP("lauraadamache.ro").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content")
				.startHint("print").endHint("voturi").endHint("retete asemanatoare"));

		wp.add(new WP("diversificare.ro").mustHaveQueries(qtbr).title(".post-heading").body(".pf-content"));

		wp.add(new WP("dulceatadetrandafiri.blogspot.").mustHaveQueries(qtbr).title(".entry-title")
				.body(".entry-content").endHint("related posts"));

		wp.add(new WP("mybabyfood.ro").mustHaveQueries(qtbr).title("h1").body(".post-entry"));

		wp.add(new WP("juniorulmeu.ro").mustHaveQueries(qtbr).title(".article-title").body(".article-content"));

		wp.add(new WP("madeline.ro").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content")
				.endHint("english version"));

		wp.add(new WP("pofta-buna.com").mustHaveQueries(qtbr).title(".entry-title").body(".td-post-content")
				.startHint("distribuie si prietenilor").endHint("spuneti-va parerea si distribuiti in retelele"));

		wp.add(new WP("aromedemamica.ro").mustHaveQueries(qtbr).title(".entry-title").body(".entry-content"));

		wp.add(new WP("costachel.ro").mustHaveQueries(qtbr)
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.title(".post-title").body(".post").endHint("visited"));

	}

	@Override
	public Recipe parseRecipe(String recipeUrl) {

		Recipe recipe = null;

		Iterator<WebsiteParserInterface> it = wp.iterator();
		boolean isParsable = false;

		while (((it.hasNext()) && (!isParsable))) {
			WebsiteParserInterface websiteParser = it.next();
			isParsable = websiteParser.isAbleToParse(recipeUrl);
			if (isParsable) {
				try {
					recipe = websiteParser.parseRecipe(recipeUrl);
				} catch (Exception e) {
					recipe = new Recipe();
					recipe.setStage(Recipe.STAGE_CANT_BE_PARSED);
					recipe.setFailureToParseReason("Parser throwed IOException: " + e.toString());
				}
			}
		}

		if (recipe == null) {
			recipe = new Recipe();
			recipe.setStage(Recipe.STAGE_CANT_BE_PARSED);
			recipe.setFailureToParseReason("No suitable parser found.");
		} else {
			recipe.setRecipeUrl(recipeUrl);
		}

		recipe = setSourceWebsite(recipe);

		return recipe;
	}

	public Recipe setSourceWebsite(Recipe recipe) {
		Recipe retRecipe = recipe;
		List<Website> websites = websiteRepository.findAll();
		boolean found = false;
		String url = recipe.getRecipeUrl();
		for (Iterator<Website> iterator = websites.iterator(); (iterator.hasNext() && (!found));) {
			Website website = iterator.next();
			if (url.contains(website.getDomain())) {
				found = true;
				retRecipe.setWebsite(website);
				retRecipe.setWebsiteNick(website.getNickname());
			}
		}

		return retRecipe;
	}

}
