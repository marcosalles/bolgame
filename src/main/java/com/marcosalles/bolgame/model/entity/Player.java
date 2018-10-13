package com.marcosalles.bolgame.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Player extends BaseEntity {

	@Id
	private String id;
	@NotBlank
	@Column(nullable = false)
	private String username;

	public Player() {
	}

	@Builder
	public Player(String id, String username) {
		this.id = id;
		this.username = username;
	}

}
