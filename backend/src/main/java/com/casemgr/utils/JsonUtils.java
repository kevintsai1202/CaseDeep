package com.casemgr.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private final static ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public static String toJson(Object obj) {
		if (obj == null)
			return null;
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
//        log.error("Occur error during parsing data to json: {}", obj, e);
			return null;
		}
	}

	public static <T> T toObject(String json, Class<T> objectClass) {
		if (json == null)
			return null;
		try {
			return objectMapper.readValue(json, objectClass);
		} catch (IOException e) {
			e.printStackTrace();
//    	log.error("Occur error during mapping json to an object: {}", json, e);
			return null;
		}
	}
}
