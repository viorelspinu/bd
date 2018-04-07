package com.vsp.bd.test.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.vsp.bd.WebSecurityConfig;
import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.service.AccountService;
import com.vsp.bd.web.controller.OthersController;

@RunWith(SpringRunner.class)
@WebMvcTest(OthersController.class)
@WebAppConfiguration
@Import(WebSecurityConfig.class)
public class OthersControllerTest {

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private BuildProperties buildProperties;

	@MockBean
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	public void testContactPage() throws Exception {

		this.mvc.perform(get("/public/contact")).andExpect(status().isOk()).andExpect(view().name("contact"))
				.andExpect(content().string(containsString("bucataruldetectiv [at] gmail [dot] com")));

	}

}
