package com.vsp.bd.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParserConfigDataRepository extends CrudRepository<ParserConfigData, Long> {

	@Query("SELECT p.value FROM ParserConfigData p WHERE (p.type = :type) ORDER BY ID ASC ")
	public List<String> findValueByTypeOrderByIdDesc(@Param("type") Integer type);

	public List<ParserConfigData> findByTypeOrderByIdDesc(Integer type);

	public ParserConfigData save(ParserConfigData parserConfigData);
	
	public void delete(Long id);
}
