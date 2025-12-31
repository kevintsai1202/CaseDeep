package com.casemgr.converter;

import java.util.List;

import com.casemgr.utils.JsonUtils;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConverterJson implements AttributeConverter<List<String>, String> {

		@Override
		public String convertToDatabaseColumn(List<String> attribute) {
			log.info("From UI:{}", attribute);
			if (attribute == null || attribute.isEmpty()) {
	            return "";
	        }
			return JsonUtils.toJson(attribute);
		}

		@Override
		public List<String> convertToEntityAttribute(String dbData) {
			log.info("From DB:{}", dbData);
			if (dbData == null || dbData.isEmpty()) {
	            return null;
	        }		
			return JsonUtils.toObject(dbData, List.class);
		}
}
