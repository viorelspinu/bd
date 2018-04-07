package com.vsp.bd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.domain.SearchTemplate;
import com.vsp.bd.domain.SearchTemplateRepository;
import com.vsp.bd.service.AccountService;

@Controller
public class AccountController extends BaseController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private SearchTemplateRepository searchTemplateRepository;

	@GetMapping("/register")
	public String viewRegister(Model model) {
		return "register";
	}

	@PostMapping("/do-register")
	public String doRegister(Model model, @RequestParam(required = true) String email,
			@RequestParam(required = true) String password) {

		if (email.isEmpty()) {
			return "register";
		}
		if (password.isEmpty()) {
			return "register";
		}

		Account oldAccount = accountService.findByEmail(email);
		if (oldAccount != null) {
			return "register";
		}

		Account account = new Account(email, password, "ROLE_USER");
		account = accountService.save(account);

		Account templateAccount = accountService.findByEmail(AccountService.TEMPLATE_ACCOUNT_USERNAME);

		for (SearchTemplate searchTemplate : templateAccount.getSearchTemplates()) {
			SearchTemplate searchTemplateNew = new SearchTemplate();
			searchTemplateNew.setAccount(account);
			searchTemplateNew.setName(searchTemplate.getName());
			searchTemplateNew.setSearchText(searchTemplate.getSearchText());
			searchTemplateRepository.save(searchTemplateNew);
		}

		accountService.signin(account);

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

}
