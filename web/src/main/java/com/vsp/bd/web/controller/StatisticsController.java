package com.vsp.bd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.domain.Log;
import com.vsp.bd.domain.LogRepository;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.domain.SearchTemplateRepository;

@Controller
public class StatisticsController extends BaseController {

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private SearchTemplateRepository searchTemplateRepository;

	@GetMapping("/delete-all-error-log")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String deleteAllErrorLog(Model model) {
		if (isFullAdmin()) {
			logRepository.deleteByType(Log.ERROR_TYPE);
		}
		return "redirect:/statistics";
	}

	@GetMapping("/delete-log")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String deleteLog(Model model, @RequestParam(required = false) Long id) {
		if (isFullAdmin()) {
			logRepository.delete(id);
		}
		return "redirect:/statistics";
	}

	@GetMapping("/reset-unparsable")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String resetUnparsable(Model model) {
		recipeRepository.resetUnparsable();
		return "redirect:/statistics";
	}

	@GetMapping("/reset-ingredients")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String resetIngredients(Model model) {
		recipeRepository.resetIngredients();
		return "redirect:/statistics";
	}

	@GetMapping("/reparse-recipes")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String reparseRecipes(Model model) {
		if (isFullAdmin()) {
			recipeRepository.reparseAll();
		}
		return "redirect:/statistics";
	}

	@GetMapping("/statistics")
	public String viewRegister(Model model) {

		model.addAttribute("totalRecipeCount", recipeRepository.count());
		model.addAttribute("ingredientsOutdatedCount", recipeRepository.countRecipeOutdatedIngredients(new Long(10)));
		model.addAttribute("recipesNotYetParsed", recipeRepository.countRecipeNotYetParsed());

		model.addAttribute("recipesCantBeParsed", recipeRepository.countRecipeCantBeParsed());

		model.addAttribute("errors", logRepository.findLogByTypeOrderByIdDesc(Log.ERROR_TYPE));

		Pageable pageRequest = new PageRequest(0, 30);
		model.addAttribute("searches", logRepository.findLogByTypeOrderByIdDesc(Log.SEARCH_TYPE, pageRequest));

		model.addAttribute("userCount", accountRepository.count());

		model.addAttribute("searchTemplateCount", searchTemplateRepository.count());

		model.addAttribute("logins", logRepository.findLogByTypeOrderByIdDesc(Log.LOGIN_TYPE));
		
		return "statistics";
	}

}
