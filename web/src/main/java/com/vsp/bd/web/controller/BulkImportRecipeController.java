package com.vsp.bd.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vsp.bd.service.RecipeService;

@Controller
public class BulkImportRecipeController extends BaseController {

	@Autowired
	private RecipeService recipeService;


	@Secured("ROLE_ADMIN")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	@GetMapping(path = "/edit/view-bulk-import-recipe")
	public String viewBulkImportRecipe(Model model) {
		clearMessages();

		model.addAttribute("urlList", "");
		return "bulk_import_recipe";
	}

	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	@PostMapping(path = "/edit/save-url-list")
	public String saveUrlList(Model model, @RequestParam(required = true) String urlList) throws IOException {
		int recipeSuccessSaved = 0;
		int urlFailed = 0;
		StringBuffer stringBuffer = new StringBuffer();
		try (BufferedReader bufferedReader = new BufferedReader(new StringReader(urlList))) {
			String line = "";
			while (line != null) {
				line = bufferedReader.readLine();
				if (line != null) {
					String[] parts = line.split("\t");
					for (int i = 0; i < parts.length; i++) {
						String string = parts[i];

						if (isUrlValid(string)) {
							if (isFullAdmin()) {
								boolean result = recipeService.saveRecipeToBeParsedLater(string);
								if (result) {
									recipeSuccessSaved++;
									stringBuffer.append(string);
									stringBuffer.append("\n\r");
								}
							}
						} else {
							urlFailed++;
						}
					}
				}
			}
		}

		setMessage(INFO_TYPE,
				"Successfully saved " + recipeSuccessSaved + " recipes. Failed to parse " + urlFailed + " items.");
		displayMessages(model);
		model.addAttribute("urlList", stringBuffer.toString() + "\n\r");
		return "bulk_import_recipe";
	}

	private boolean isUrlValid(String url) {
		try {
			URI uri = new URI(url);
			if (uri.getHost() != null) {
				if (uri.getPath() != null) {
					if (uri.getPath().length() > 0) {
						if (uri.getHost().length() > 0) {
							if (!url.endsWith("jpg")) {
								if (!url.endsWith("png")) {
									return true;
								}
							}
						}
					}
				}
			}
		} catch (URISyntaxException e) {
			return false;
		}

		return false;

	}
}