package com.vsp.bd.service;

public final class StringHelper {

	private StringHelper() {

	}

	public static final String removeDiacritics(String str) {
		return str.replaceAll("Ă", "A").replaceAll("ă", "a").replaceAll("Î", "I").replaceAll("î", "i")
				.replaceAll("Ș", "S").replaceAll("ș", "s").replaceAll("Ț", "T").replaceAll("ț", "t")
				.replaceAll("Â", "A").replaceAll("â", "a").replaceAll("ş", "s").replaceAll("ţ", "t");

	}

}
