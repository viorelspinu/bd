package com.vsp.bd.domain;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchTemplateRepository extends CrudRepository<SearchTemplate, Integer> {

	public Set<SearchTemplate> findByNameIgnoreCaseAndAccount_Id(String name, Long accountId);

	public Set<SearchTemplate> findByAccountId(Long id);

}
