package com.vsp.bd.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
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
import com.vsp.bd.domain.Recipe;
import com.vsp.bd.domain.RecipeRepository;
import com.vsp.bd.domain.SearchTemplateRepository;
import com.vsp.bd.domain.Tag;
import com.vsp.bd.domain.TagRepository;
import com.vsp.bd.service.AccountService;
import com.vsp.bd.service.IngredientService;
import com.vsp.bd.service.RecipeService;
import com.vsp.bd.service.RecipeServiceImpl;
import com.vsp.bd.service.parser.ParserService;

@RunWith(SpringRunner.class)
public class RecipeServiceTest {

	@TestConfiguration
	static class RecipeServiceImplTestContextConfiguration {

		@Bean
		public RecipeService recipeService() {
			return new RecipeServiceImpl();
		}
	}

	@MockBean
	private BuildProperties buildProperties;

	@MockBean
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private RecipeRepository recipeRepository;

	@MockBean
	private IngredientService ingredientService;

	@MockBean
	private SearchTemplateRepository searchTemplateRepository;

	@MockBean
	private ParserService parserService;

	@MockBean
	private TagRepository tagRepository;

	@Autowired
	private RecipeService recipeService;

	private Ingredient marar;

	private Ingredient ou;

	private Ingredient telina;

	private Ingredient oua;

	private Ingredient leustean;

	private Recipe recipe;

	private Tag tag1;

	private Tag tag2;

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

		recipe = new Recipe();
		recipe.setTitle("title");
		recipe.setText("text");
		recipe.setStage(Recipe.PARSED_FROM_WEBSITE);
		recipe.setRecipeUrl("http://www.adihadean.ro/url-1");
		recipe.setId(1);
		Set<Tag> tagSet = new HashSet<>();
		recipe.setTags(tagSet);

		tag1 = new Tag();
		tag1.setId(1);
		tag1.setName("tag1");

		tag2 = new Tag();
		tag2.setId(2);
		tag2.setName("tag2");

	}

	@Test
	public void testSaveRecipeToBeParsedLaterAlreadyInDB() throws Exception {
		String url = "http://www.adihadean.ro/reteta-1";
		Recipe recipe = new Recipe();

		when(recipeRepository.findOneByRecipeUrl(any(String.class))).thenReturn(recipe);

		boolean ret = recipeService.saveRecipeToBeParsedLater(url);
		assertFalse(ret);
		verify(recipeRepository, Mockito.times(0)).save(any(Recipe.class));
	}

	@Test
	public void testSaveRecipeToBeParsedLaterNew() throws Exception {
		String url = "http://www.adihadean.ro/reteta-1";

		when(recipeRepository.findOneByRecipeUrl(any(String.class))).thenReturn(null);

		boolean ret = recipeService.saveRecipeToBeParsedLater(url);
		assertTrue(ret);

		ArgumentCaptor<Recipe> argument = ArgumentCaptor.forClass(Recipe.class);
		verify(recipeRepository, Mockito.times(1)).save(argument.capture());

		assertEquals(url, argument.getValue().getRecipeUrl());
		assertEquals(new Byte(Recipe.STAGE_URL_SAVED), argument.getValue().getStage());
		assertEquals(Recipe.PARSED_FROM_WEBSITE, argument.getValue().getSourceType());

	}

	@Test
	public void testAddTagsToRecipe() throws Exception {

		when(tagRepository.findById(tag1.getId())).thenReturn(tag1);
		when(tagRepository.findById(tag2.getId())).thenReturn(tag2);

		when(recipeRepository.findById(recipe.getId())).thenReturn(recipe);

		Set<Integer> tagsId = new HashSet<>();
		tagsId.add(tag1.getId());
		tagsId.add(tag2.getId());

		Set<Integer> recipeIdSet = new HashSet<>();
		recipeIdSet.add(recipe.getId());

		recipeService.addTagsToRecipes(recipeIdSet, tagsId);

		assertEquals(tagsId.size(), recipe.getTags().size());

		assertTrue(recipe.getTags().contains(tag1));
		assertTrue(recipe.getTags().contains(tag2));

	}

}
