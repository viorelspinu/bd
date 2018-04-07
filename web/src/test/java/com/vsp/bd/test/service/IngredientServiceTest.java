package com.vsp.bd.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.domain.Ingredient;
import com.vsp.bd.domain.IngredientRepository;
import com.vsp.bd.service.AccountService;
import com.vsp.bd.service.IngredientService;
import com.vsp.bd.service.IngredientServiceImpl;
import static org.mockito.Matchers.*;

@RunWith(SpringRunner.class)
public class IngredientServiceTest {

	@TestConfiguration
	static class IngredientServiceImplTestContextConfiguration {

		@Bean
		public IngredientService employeeService() {
			return new IngredientServiceImpl();
		}
	}

	@MockBean
	private BuildProperties buildProperties;

	@MockBean
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private IngredientRepository ingredientRepository;

	@Autowired
	private IngredientService ingredientService;

	private Ingredient marar;

	private Ingredient ou;

	private Ingredient telina;

	private Ingredient oua;

	private Ingredient leustean;

	@Before
	public void setup() {

		marar = new Ingredient();
		marar.setName("marar");
		marar.setId(1);

		ou = new Ingredient();
		ou.setName("ou");
		ou.setId(2);
		ou.setSynonymIds("4");

		telina = new Ingredient();
		telina.setName("telina");
		telina.setId(3);

		oua = new Ingredient();
		oua.setName("oua");
		oua.setId(4);

		leustean = new Ingredient();
		leustean.setName("leustean");
		leustean.setId(5);

	}

	@Test
	public void testSaveIngredientsIfNotThereYet() throws Exception {
		Set<String> names = new HashSet<>();
		names.add("MaRar");
		names.add("    patrunjel");
		names.add("leustean");

		Set<Ingredient> retIngredients = new HashSet<>();
		retIngredients.add(marar);
		retIngredients.add(leustean);

		when(ingredientRepository.findIngredientByNameIsIn(any(Set.class))).thenReturn(retIngredients);

		ArgumentCaptor<Ingredient> argument = ArgumentCaptor.forClass(Ingredient.class);

		ingredientService.saveIngredientsIfNotThereYet(names);

		verify(ingredientRepository, Mockito.times(1)).save(argument.capture());
		assertEquals("patrunjel", argument.getAllValues().get(0).getName());

	}

	@Test
	public void testfindIngredientsInRecipeTextWithoutSynonyms() throws Exception {

		List<Ingredient> dbIngredients = new ArrayList<Ingredient>();

		dbIngredients.add(marar);
		dbIngredients.add(leustean);
		dbIngredients.add(telina);

		when(ingredientRepository.findAll()).thenReturn(dbIngredients);

		Set<Ingredient> ingredientsInRecipe = ingredientService
				.findIngredientsInRecipeText("se ia niste marar si se amesteca cu leustean.");

		assertEquals(2, ingredientsInRecipe.size());

		assertTrue(ingredientsInRecipe.contains(marar));
		assertTrue(ingredientsInRecipe.contains(leustean));
		assertFalse(ingredientsInRecipe.contains(telina));

	}

	@Test
	public void testfindIngredientsInRecipeTextWitSynonyms() throws Exception {

		List<Ingredient> dbIngredients = new ArrayList<Ingredient>();
		dbIngredients.add(marar);
		dbIngredients.add(leustean);
		dbIngredients.add(ou);
		dbIngredients.add(oua);
		dbIngredients.add(telina);

		when(ingredientRepository.findAll()).thenReturn(dbIngredients);
		when(ingredientRepository.findById(oua.getId())).thenReturn(oua);

		Set<Ingredient> ingredientsInRecipe = ingredientService
				.findIngredientsInRecipeText("se ia niste marar si se amesteca cu leustean, apoi se pune un ou batut.");

		assertEquals(4, ingredientsInRecipe.size());

		assertTrue(ingredientsInRecipe.contains(marar));
		assertTrue(ingredientsInRecipe.contains(leustean));
		assertTrue(ingredientsInRecipe.contains(ou));
		assertTrue(ingredientsInRecipe.contains(oua));
		assertFalse(ingredientsInRecipe.contains(telina));

	}

	@Test
	public void testLoadIngredientsFromString() throws Exception {

		Set<Ingredient> dbIngredients = new HashSet();
		dbIngredients.add(marar);
		dbIngredients.add(telina);

		when(ingredientRepository.findIngredientByNameIsIn(any(Set.class))).thenReturn(dbIngredients);

		Set<Ingredient> ingredients = ingredientService.loadIngredientsFromString("marar,telina,oua");
		assertEquals(2, ingredients.size());
		assertTrue(ingredients.contains(marar));
		assertTrue(ingredients.contains(telina));

		ArgumentCaptor<Ingredient> argument = ArgumentCaptor.forClass(Ingredient.class);

		verify(ingredientRepository, Mockito.times(1)).save(argument.capture());

		assertEquals(oua.getName(), argument.getValue().getName());
	}

}
