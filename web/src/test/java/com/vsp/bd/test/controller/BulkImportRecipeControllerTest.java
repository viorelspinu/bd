package com.vsp.bd.test.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.vsp.bd.WebSecurityConfig;
import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.domain.SearchTemplate;
import com.vsp.bd.service.AccountService;
import com.vsp.bd.service.RecipeService;
import com.vsp.bd.web.controller.BulkImportRecipeController;

@RunWith(SpringRunner.class)
@WebMvcTest(BulkImportRecipeController.class)
@WebAppConfiguration
@Import(WebSecurityConfig.class)
public class BulkImportRecipeControllerTest {

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private AccountService accountService;

	@MockBean
	private BuildProperties buildProperties;

	@MockBean
	private RecipeService recipeService;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

	}

	@Test
	public void testViewBulkImportRecipeNotAuth() throws Exception {
		this.mvc.perform(get("/edit/view-bulk-import-recipe")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUserRegular
	public void testViewBulkImportRecipeRegularUser() throws Exception {
		this.mvc.perform(get("/edit/view-bulk-import-recipe")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockAdmin
	public void testViewBulkImportRecipeAdminUser() throws Exception {
		this.mvc.perform(get("/edit/view-bulk-import-recipe")).andExpect(status().isOk());
	}

	@Test
	@WithMockAdmin
	public void testBulkImportRecipeOK() throws Exception {

		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		String URL1 = "http://www.adihadean.ro/abc";
		String URL2 = "http://www.adihadean.ro/efg";
		String WRONG_URL = "wrpqiebfioub";

		this.mvc.perform(post("/edit/save-url-list").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("urlList", URL1 + "\n" + URL2 + "\n" + WRONG_URL)).andExpect(status().isOk())
				.andExpect(view().name("bulk_import_recipe"));

		verify(recipeService, Mockito.times(2)).saveRecipeToBeParsedLater(argument.capture());
		assertEquals(URL1, argument.getAllValues().get(0));
		assertEquals(URL2, argument.getAllValues().get(1));
	}

}
