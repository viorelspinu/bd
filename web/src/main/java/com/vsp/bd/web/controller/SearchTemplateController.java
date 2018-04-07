package com.vsp.bd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.SearchTemplate;
import com.vsp.bd.domain.SearchTemplateRepository;

@Controller
public class SearchTemplateController extends BaseController {

	@Autowired
	private SearchTemplateRepository searchTemplateRepository;

	@PostMapping(path = "/public/save-search-template")
	@ResponseBody
	public String saveSearchTemplate(@RequestParam(required = true) String searchText,
			@RequestParam(required = true) String templateName) {
		SearchTemplate searchTemplate = new SearchTemplate();
		searchTemplate.setSearchText(searchText);
		searchTemplate.setName(templateName);

		Account account = getLoggedInAccount();
		searchTemplate.setAccount(account);
		searchTemplateRepository.save(searchTemplate);

		return "OK";
	}

	@PostMapping(path = "/delete-search-template")
	@ResponseBody
	public String deleteSearchTemplate(@RequestParam(required = true) Integer id) {
		SearchTemplate searchTemplate = searchTemplateRepository.findOne(id);
		if (getLoggedInAccount() == null) {
			return "AUTH_ERROR";
		}
		if (searchTemplate.getAccount().getId() != getLoggedInAccount().getId()) {
			return "AUTH_ERROR";
		}

		searchTemplateRepository.delete(id);
		return "OK";
	}

	@GetMapping(path = "/public/get-search-templates")
	@ResponseBody
	public SearchTemplatesArray getSearchTemplates() {

		SearchTemplatesArray searchTemplatesArray = new SearchTemplatesArray();

		Account account = getLoggedInAccount();
		if (account != null) {
			Iterable<SearchTemplate> searchTemplates = searchTemplateRepository
					.findByAccountId(getLoggedInAccount().getId());

			searchTemplatesArray.setSearchTemplates(searchTemplates);
		}
		return searchTemplatesArray;

	}
}

class SearchTemplatesArray {

	private Iterable<SearchTemplate> searchTemplates;

	public Iterable<SearchTemplate> getSearchTemplates() {
		return searchTemplates;
	}

	public void setSearchTemplates(Iterable<SearchTemplate> searchTemplates) {
		this.searchTemplates = searchTemplates;
	}

}