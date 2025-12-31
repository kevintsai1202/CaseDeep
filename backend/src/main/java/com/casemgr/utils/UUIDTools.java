package com.casemgr.utils;

import java.util.UUID;

public class UUIDTools {
	 public static String getUUID() {
	        UUID uuid = UUID.randomUUID();
	        return uuid.toString().replaceAll("-", "");
	    }
}
