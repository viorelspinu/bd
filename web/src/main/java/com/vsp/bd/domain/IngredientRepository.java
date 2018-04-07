package com.vsp.bd.domain;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

	public Ingredient findOneByName(String name);

	public Set<Ingredient> findIngredientByName(String name);

	public Set<Ingredient> findIngredientByNameIsIn(Set<String> nameSet);

	public Ingredient findById(Integer id);

	@Query("SELECT i from Ingredient i ORDER BY name")
	public Iterable<Ingredient> findAllOrderByName();

	//@Cacheable(CachingConfig.INGREDIENTS)
	public List<Ingredient> findAll();

}
