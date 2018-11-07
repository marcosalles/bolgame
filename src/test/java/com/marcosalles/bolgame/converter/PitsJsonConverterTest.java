package com.marcosalles.bolgame.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

class PitsJsonConverterTest {

	private PitsJsonConverter converter;

	@BeforeEach
	void setUp() {
		converter = new PitsJsonConverter();
	}

	@Test
	void convertToDatabaseColumn__should_convert_map_to_json_string() {
		Map<String, Integer> map = Map.of(
			"key-two", 2,
			"key-one", 1
		);

		String convertedMap = converter.convertToDatabaseColumn(map);

		assertThat(convertedMap, is(oneOf("{\"key-one\":1,\"key-two\":2}", "{\"key-two\":2,\"key-one\":1}")));
	}

	@Test
	void convertToDatabaseColumn__should_return_null_for_null_map() {
		String convertedString = converter.convertToDatabaseColumn(null);
		assertThat(convertedString, is(nullValue()));
	}

	@Test
	void convertToEntityAttribute__should_return_map_with_values() {
		Map<String, Integer> convertedMap = converter.convertToEntityAttribute("{\"key-one\":1,\"key-two\":2,\"key-three\":3}");

		assertThat(convertedMap, is(Map.of(
			"key-two", 2,
			"key-one", 1,
			"key-three", 3
		)));
	}
}