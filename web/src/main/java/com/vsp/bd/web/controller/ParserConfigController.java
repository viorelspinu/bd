package com.vsp.bd.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vsp.bd.domain.ParserConfigData;
import com.vsp.bd.domain.ParserConfigDataRepository;
import com.vsp.bd.service.StringHelper;
import com.vsp.bd.service.parser.ParserServiceImpl;

@Controller
public class ParserConfigController extends BaseController {

	@Autowired
	private ParserConfigDataRepository parserConfigDataRepository;

	@Autowired
	private ParserServiceImpl parserServiceImpl;

	@PostMapping(path = "/edit/add-new-must-query")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String addNewMustQuery(Model model, @RequestParam(required = true) String newValue) {

		if (isFullAdmin()) {
			if (newValue.length() > 0) {
				ParserConfigData parserConfigData = new ParserConfigData();
				newValue = newValue.toLowerCase();
				newValue = StringHelper.removeDiacritics(newValue);
				parserConfigData.setValue(newValue);
				parserConfigData.setType(ParserConfigData.MUST_HAVE_TO_BE_RECIPE_QUERY);
				parserConfigDataRepository.save(parserConfigData);
			}

			parserServiceImpl.initWebParsers();
		}
		return "redirect:/edit/view-parser-config";
	}

	@GetMapping(path = "/edit/delete-must-query")
	@PreAuthorize("hasRole('ADMIN') OR hasRole('ADMIN_DEMO')")
	public String addNewMustQuery(Model model, @RequestParam(required = true) Long id) {

		if (isFullAdmin()) {
			parserConfigDataRepository.delete(id);
		}
		return "redirect:/edit/view-parser-config";
	}

	@GetMapping(path = "/edit/view-parser-config")
	public String viewParserConfig(Model model) {

		List<ParserConfigData> parserConfigDatas = parserConfigDataRepository
				.findByTypeOrderByIdDesc(ParserConfigData.MUST_HAVE_TO_BE_RECIPE_QUERY);

		model.addAttribute("mustHaveQueries", parserConfigDatas);

		return "parser_config";

	}

}
