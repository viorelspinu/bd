package com.vsp.bd.service.schedulled;

import org.springframework.beans.factory.annotation.Value;

public class GenericSchedulledProcess {

	@Value("${localhost.machine:FALSE}")
	private String isLocalhost;

	private static final String TRUE = "TRUE";

	protected boolean isLocalhost() {

		if (TRUE.equals(isLocalhost)) {
			return true;
		}

		return false;
	}
}
