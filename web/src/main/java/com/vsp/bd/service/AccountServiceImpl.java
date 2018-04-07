package com.vsp.bd.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.domain.Log;
import com.vsp.bd.domain.LogRepository;

@Component("userService")
@Transactional
public class AccountServiceImpl implements AccountService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private LogRepository logRepository;

	private void logAuthentication(String username) {
		if ("viorel.spinu@gmail.com".equals(username)) {
			return;
		}
		Log log = new Log();
		log.setType(Log.LOGIN_TYPE);
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.UK)
				.withZone(ZoneId.of("Europe/Bucharest"));

		try {
			log.setValue(formatter.format(Instant.now()) + "  " + username.substring(0, 2) + "[...]"
					+ username.substring(username.indexOf("@"), username.length()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		logRepository.save(log);
	}

	private List<GrantedAuthority> createAuthority(Account account) {
		String[] roles = account.getRole().split(",");
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles.length);
		for (String role : roles) {
			role = role.trim();
			grantedAuthorities.add(new SimpleGrantedAuthority(role));
		}
		return grantedAuthorities;
	}

	private User createUser(Account account) {
		return new UserWithId(account.getId(), account.getEmail(), account.getPassword(), createAuthority(account));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		logAuthentication(username);

		Account account = accountRepository.findOneByEmail(username);
		if (account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
	}

	@Override
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setCreated(Instant.now());
		return accountRepository.save(account);
	}

	@Override
	public boolean existUserWithEmail(String email) {
		Account account = accountRepository.findByEmail(email);
		return account != null;
	}

	@Override
	public Account findAccount(Long id) {

		Account result = accountRepository.findById(id);

		return result;
	}

	@Override
	public void changePassword(Account user, String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));

	}

	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null, createAuthority(account));
	}

	@Override
	public Account findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	@Override
	public Account findCurrentAccount() {
		String email = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		Account account = findByEmail(email);
		return account;
	}

	@Override
	public Iterable<Account> findAll() {
		return accountRepository.findAll();
	}

}
