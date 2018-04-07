package com.vsp.bd.domain;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long>, RecipeRepositoryCustom {

	public Set<Recipe> findByTitleLike(String title);

	public Recipe save(Recipe recipe);

	public Recipe findById(Integer id);

	public Set<Recipe> findByRecipeUrl(String url);

	public Recipe findOneByRecipeUrl(String url);

	@Query("SELECT id FROM Recipe r WHERE ( (size(r.tags) = 0) AND (r.stage = " + Recipe.STAGE_PARSED + ")) ORDER by RAND()")
	public List<Integer> findRecipeWithNoTags(Pageable pageable);
	
	@Query("SELECT id FROM Recipe r WHERE (r.stage = " + Recipe.STAGE_URL_SAVED + ") ORDER by RAND()")
	public List<Integer> findRecipeNotYetParsed(Pageable pageable);

	@Query("SELECT id FROM Recipe r WHERE ((r.stage = " + Recipe.STAGE_PARSED + ")"
			+ " AND ((r.lastUpdateTimestamp < :timestamp) OR (r.lastUpdateTimestamp IS NULL)))")
	public List<Integer> findRecipeOutdatedIngredients(@Param("timestamp") Long timestamp, Pageable pageable);

	public List<Recipe> findRecipeByStageOrderById(Byte stage, Pageable pageable);

	@Query("SELECT COUNT(id) FROM Recipe r WHERE ((r.stage = " + Recipe.STAGE_PARSED + ")"
			+ " AND ((r.lastUpdateTimestamp < :timestamp) OR (r.lastUpdateTimestamp IS NULL)))")
	public Integer countRecipeOutdatedIngredients(@Param("timestamp") Long timestamp);

	@Query("SELECT COUNT(id) FROM Recipe r WHERE (r.stage = " + Recipe.STAGE_URL_SAVED + ")")
	public Integer countRecipeNotYetParsed();

	@Query("SELECT COUNT(id) FROM Recipe r WHERE (r.stage = " + Recipe.STAGE_CANT_BE_PARSED + ")")
	public Integer countRecipeCantBeParsed();

	@Transactional
	@Modifying
	@Query("UPDATE Recipe r SET stage=" + Recipe.STAGE_URL_SAVED + " WHERE r.stage = " + Recipe.STAGE_CANT_BE_PARSED)
	public Integer resetUnparsable();

	@Transactional
	@Modifying
	@Query("UPDATE Recipe r SET lastUpdateTimestamp = 0 WHERE r.stage = " + Recipe.STAGE_PARSED)
	public Integer resetIngredients();

	@Transactional
	@Modifying
	@Query("UPDATE Recipe r SET stage = " + Recipe.STAGE_URL_SAVED + " WHERE r.stage = " + Recipe.STAGE_PARSED)
	public Integer reparseAll();

	public long count();

}
