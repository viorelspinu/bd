package com.vsp.bd.domain;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

	public Tag findOneByName(String name);

	public Set<Tag> findIngredientByName(String name);

	public Set<Tag> findTagByNameIsIn(Set<String> nameSet);

	public Tag findById(Integer id);

	@Query("SELECT t from Tag t ORDER BY name")
	public Iterable<Tag> findAllOrderByName();

	public List<Tag> findAll();

}
