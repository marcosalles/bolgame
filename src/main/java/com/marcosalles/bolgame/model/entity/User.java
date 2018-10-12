package com.marcosalles.bolgame.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

	private String id;
	private String username;

	public User() {
	}

	@Builder
	public User(String id, String username) {
		this.id = id;
		this.username = username;
	}

}
