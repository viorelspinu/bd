package com.vsp.bd.web.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.vsp.bd.domain.Account;
import com.vsp.bd.domain.AccountRepository;
import com.vsp.bd.service.UserWithId;

public class BaseController {

	@Value("${dev.mode:TRUE}")
	private String devMode;

	@Autowired
	private AccountRepository accountRepository;

	private String messages;

	private byte messageType;

	public static final byte NONE_TYPE = 0;
	public static final byte ERROR_TYPE = 1;
	public static final byte INFO_TYPE = 2;

	protected void displayMessages(Model model) {
		model.addAttribute("messageType", messageType);
		model.addAttribute("messages", messages);
		if (!"TRUE".equals(devMode)) {
			devMode = null;
		}
		model.addAttribute("devMode", devMode);
	}

	protected void setMessage(byte type, String message) {
		this.messages = message;
		this.messageType = type;

	}

	protected void clearMessages() {
		this.messages = "";
		this.messageType = NONE_TYPE;
	}

	protected Account getLoggedInAccount() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserWithId) {
			UserWithId userWithId = (UserWithId) authentication.getPrincipal();
			Account account = accountRepository.findById(userWithId.getId());
			return account;
		}
		return null;

	}

	protected Collection<? extends GrantedAuthority> getLoggedInUserRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getAuthorities();
	}

	private boolean hasRole(String role) {
		Collection<? extends GrantedAuthority> roles = getLoggedInUserRoles();
		for (GrantedAuthority grantedAuthority : roles) {
			if (role.equals(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}

	protected boolean isFullAdmin() {
		return hasRole(Account.ROLE_ADMIN);
	}

	protected boolean isDemoAdmin() {
		return hasRole(Account.ROLE_ADMIN_DEMO);
	}

}
