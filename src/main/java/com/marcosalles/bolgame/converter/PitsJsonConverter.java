package com.marcosalles.bolgame.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Converter(autoApply = true)
public class PitsJsonConverter implements AttributeConverter<Map<String, Integer>, String> {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, Integer> map) {
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Integer> convertToEntityAttribute(String string) {
		try {
			return mapper.readValue(string, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Map.of();
	}
}
