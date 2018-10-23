package com.marcosalles.bolgame.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

@Getter
public class Message implements Serializable {

	private String playerId = "";
	private String contents = "";

	public <T> Optional<T> getContentsAs(Class<T> contentClass) {
		try {
			return Optional.ofNullable(new ObjectMapper().readValue(this.contents, contentClass));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
