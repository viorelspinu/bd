package com.vsp.bd.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OthersController extends BaseController {

	@GetMapping("/public/contact")
	public String viewContact(Model model) {

		return "contact";
	}

}
