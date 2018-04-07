package com.vsp.bd.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

	Account findByEmail(String email);

	Account findByEmailAndPassword(String email, String digestPassword);

	Account findOneByEmail(String username);
	
	Account findById(Long id);
	
	long count();

}
