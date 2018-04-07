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
import com.vsp.bd.domain.SearchTemplateRepository;
import com.vsp.bd.service.AccountService;
import com.vsp.bd.web.controller.AccountController;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
@WebAppConfiguration
@Import(WebSecurityConfig.class)
public class AccountControllerTest {

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private BuildProperties buildProperties;

	@MockBean
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private SearchTemplateRepository searchTemplateRepository;

	private MockMvc mvc;

	private Account testAccount;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		testAccount = new Account();
		testAccount.setEmail("test@test.com");
		testAccount.setPassword("passwd");
		testAccount.setRole("ROLE_USER");
	}

	@Test
	public void testViewLogin() throws Exception {
		this.mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	public void testViewRegister() throws Exception {
		this.mvc.perform(get("/register")).andExpect(status().isOk()).andExpect(view().name("register"));
	}

	@Test
	public void testCreateUserAlreadyExists() throws Exception {

		when(accountService.findByEmail(testAccount.getEmail())).thenReturn(testAccount);

		this.mvc.perform(post("/do-register").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", testAccount.getEmail())
				.param("password", testAccount.getPassword())).andExpect(status().isOk())
				.andExpect(view().name("register"));

		verify(accountRepository, Mockito.times(0)).save(testAccount);
		verify(accountRepository, Mockito.times(0)).findByEmail(testAccount.getEmail());
	}

	@Test
	public void testCreateUserEmptyEmail() throws Exception {

		when(accountService.findByEmail(testAccount.getEmail())).thenReturn(null);

		this.mvc.perform(post("/do-register").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "")
				.param("password", testAccount.getPassword())).andExpect(status().isOk())
				.andExpect(view().name("register"));

		verify(accountRepository, Mockito.times(0)).save(testAccount);

	}

	@Test
	public void testCreateUserEmptyPassword() throws Exception {

		when(accountService.findByEmail(testAccount.getEmail())).thenReturn(null);

		this.mvc.perform(post("/do-register").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", testAccount.getEmail())
				.param("password", "")).andExpect(status().isOk()).andExpect(view().name("register"));

		verify(accountRepository, Mockito.times(0)).save(testAccount);

	}

	@Test
	public void testCreateUserAllOK() throws Exception {

		when(accountService.findByEmail(testAccount.getEmail())).thenReturn(null);

		ArgumentCaptor<Account> argument = ArgumentCaptor.forClass(Account.class);

		Account templateAccount = new Account();
		Set<SearchTemplate> searchTemplates = new HashSet<>();
		templateAccount.setSearchTemplates(searchTemplates);
		templateAccount.setEmail(AccountService.TEMPLATE_ACCOUNT_USERNAME);
		when(accountService.findByEmail(AccountService.TEMPLATE_ACCOUNT_USERNAME)).thenReturn(templateAccount);

		this.mvc.perform(post("/do-register").with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", testAccount.getEmail())
				.param("password", testAccount.getPassword())).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));

		verify(accountService, Mockito.times(1)).save(argument.capture());
		assertEquals(testAccount.getEmail(), argument.getValue().getEmail());
		assertEquals(testAccount.getPassword(), argument.getValue().getPassword());
		assertEquals(testAccount.getRole(), argument.getValue().getRole());

	}
}
