package com.marcosalles.bolgame.model.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.io.Serializable;

@Getter
public class Message implements Serializable {

	private String playerId = "";
	private String contents = "";

	public <T> T getContentsAs(Class<T> contentClass) throws IOException {
		return new ObjectMapper().readValue(this.contents, contentClass);
	}
}
