package com.marcosalles.bolgame.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter(autoApply = true)
public class PitsJsonConverter implements AttributeConverter<Map<String, Integer>, String> {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, Integer> map) {
		if (map == null) {
			return null;
		}

		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Integer> convertToEntityAttribute(String string) {
		try {
			return mapper.readValue(string, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Map.of();
	}
}
