package com.java110.things.util;

import java.util.UUID;

public class IDUtils {
	public static String id() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

	public static String idLow() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
	
}
