package com.vsp.bd.domain;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LogRepository extends CrudRepository<Log, Long> {

	
	public List<Log> findLogByTypeOrderByIdDesc(String type, Pageable pageable);
	
	public List<Log> findLogByTypeOrderByIdDesc(String type);
	
	public void deleteByType(String type);
}
