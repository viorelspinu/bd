package com.vsp.bd.test.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithMockUser;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "test@test.com", roles = { "ADMIN", "USER" })
public @interface WithMockAdmin {
}
