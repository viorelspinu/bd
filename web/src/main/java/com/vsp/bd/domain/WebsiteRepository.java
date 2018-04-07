package com.vsp.bd.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface WebsiteRepository extends CrudRepository<Website, Long> {

	@Query("SELECT w from Website w ORDER BY domain")
	public List<Website> findAllOrderByDomain();
	
	public List<Website> findAll();

}
