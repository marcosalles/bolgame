package com.marcosalles.bolgame.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@Table(name = "players")
@EqualsAndHashCode
@NoArgsConstructor
public class Player extends BaseEntity {

	@Id
	private String id;

	@NotBlank
	@Column(nullable = false)
	private String username;

	@Builder
	public Player(String id, String username) {
		this.id = id;
		this.username = username;
	}

}
