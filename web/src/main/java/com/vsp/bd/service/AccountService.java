package com.vsp.bd.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.vsp.bd.domain.Account;

public interface AccountService extends UserDetailsService {

	public static final String TEMPLATE_ACCOUNT_USERNAME = "template_account";

	Account save(Account account);

	boolean existUserWithEmail(String email);

	Account findAccount(Long id);

	void changePassword(Account user, String password);

	void signin(Account account);

	Account findByEmail(String email);

	Account findCurrentAccount();

	Iterable<Account> findAll();

}
